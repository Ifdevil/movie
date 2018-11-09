package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.TokenBucket;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/order/")
public class OrderController {

    private static TokenBucket tokenBucket = new TokenBucket();

    @Reference(interfaceClass = OrderServiceAPI.class,check = false,group = "order2018")
    private OrderServiceAPI orderServiceAPI;

    @Reference(interfaceClass = OrderServiceAPI.class,check = false,group = "order2017")
    private OrderServiceAPI orderServiceAPI2017;

    public ResponseVO error(Integer fieldId,String soldSeats,String seatsName){
        return ResponseVO.serviceFail("抱歉，下单的人太多了，请稍后再试！");
    }
    /**
     * 购票
     * @param fieldId
     * @param soldSeats
     * @param seatsName
     * @return
     */
    @HystrixCommand(fallbackMethod = "error", commandProperties = {
            @HystrixProperty(name="execution.isolation.strategy", value = "THREAD"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value= "4000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
                    }, threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "1"),
                    @HystrixProperty(name = "maxQueueSize", value = "10"),
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
            })
    @RequestMapping(value = "buyTickets",method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId,String soldSeats,String seatsName){
        try {

            if(tokenBucket.getToken()){
                //验证售出的票是否为真
                boolean isTrue = orderServiceAPI.isTrueSeats(fieldId+"",soldSeats);

                //已经销售的座位里，有没有这些座位
                boolean isNotSold = orderServiceAPI.isNotSoldSeats(fieldId+"",soldSeats);

                // 验证，上述两个内容有一个不为真，则不创建订单信息
                if(isTrue && isNotSold){

                    //创建订单信息，注意获取登录人
                    String userId = CurrentUser.getCurrentUser();
                    if(userId == null && userId.trim().length()==0) {
                        return ResponseVO.serviceFail("用户未登录");
                    }
                    OrderVO orderVO = orderServiceAPI.saveOrderInfo(fieldId,soldSeats,seatsName,Integer.parseInt(userId));
                    if(orderVO==null){
                        log.error("购票未成功");
                        return ResponseVO.serviceFail("购票业务异常");
                    }else{
                        return ResponseVO.success(orderVO);
                    }
                }else{
                    return ResponseVO.serviceFail("订单中的座位编号有问题");
                }
            }else{
                return ResponseVO.serviceFail("够票人数过多，请稍后再试");
            }

        }catch (Exception e){
            log.error("购票业务异常",e);
            return ResponseVO.serviceFail("购票业务异常");
        }
    }


    @RequestMapping(value = "getOrderInfo",method = RequestMethod.POST)
    public ResponseVO getOrderInfo(
            @RequestParam(name = "nowPage",required = false,defaultValue = "1")Integer nowPage,
            @RequestParam(name = "pageSize",required = false,defaultValue = "5")Integer pageSize
            ){

        //获取当前登录人的信息
        String userId = CurrentUser.getCurrentUser();
        //根据个人信息获取订单信息
        Page<OrderVO> page = new Page<>(nowPage,pageSize);
        if(userId != null && userId.trim().length()>0) {
            Page<OrderVO> result = orderServiceAPI.getOrderByUserId(Integer.parseInt(userId), page);
            Page<OrderVO> result2017 = orderServiceAPI2017.getOrderByUserId(Integer.parseInt(userId), page);
            //合并结果
            int totalPages = (int)(result.getPages()+result2017.getPages());
            //合并2018和2017订单总数
            List<OrderVO> orderVOList = new ArrayList<>();
            orderVOList.addAll(result.getRecords());
            orderVOList.addAll(result2017.getRecords());
            return ResponseVO.success(nowPage,totalPages,"",orderVOList);

        }else {
            return ResponseVO.serviceFail("用户未登录");
        }
    }
}
