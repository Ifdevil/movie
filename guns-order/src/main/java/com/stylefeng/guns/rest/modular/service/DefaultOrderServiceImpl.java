package com.stylefeng.guns.rest.modular.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.OrderServiceAPI;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Service(interfaceClass = OrderServiceAPI.class,group = "default")
public class DefaultOrderServiceImpl implements OrderServiceAPI {

    @Autowired
    private MoocOrderTMapper moocOrderTMapper;

    @Reference(interfaceClass = CinemaServiceAPI.class,check = false)
    private CinemaServiceAPI cinemaServiceAPI;

    @Autowired
    private FTPUtil ftpUtil;

    //验证是否为真实的座位编号
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {

        //根据FieldId找到对应的座位位置图
        String seatPath = moocOrderTMapper.getSeatsByFieldId(fieldId);
        //读取位置图，判断seats是否为真
        String fileStrByAddress = ftpUtil.getFileStrByAddress(seatPath);

        //将fileStr转换为JSON对象
        JSONObject jsonObject = JSON.parseObject(fileStrByAddress);
        String ids = jsonObject.get("ids").toString();
        String[] seatArrs = seats.split(",");
        String[] idArrs = ids.split(",");
        int isTrue = 0;
        for (String id:idArrs){
            for (String seat:seatArrs){
                if(seat.equalsIgnoreCase(id)){
                    isTrue++;
                }
            }
        }
        if (seatArrs.length == isTrue){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 判断是否为已售座位
     * @param fieldId
     * @param seats
     * @return
     */
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("field_id",fieldId);
        List<MoocOrderT> list = moocOrderTMapper.selectList(entityWrapper);
        String[] seatArrs = seats.split(",");
        for (MoocOrderT moocOrderT:list){
            String[] ids = moocOrderT.getSeatsIds().split(",");
            for (String id:ids){
                for (String seat:seatArrs){
                    if(id.equalsIgnoreCase(seat)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //创建新订单
    @Override
    public OrderVO saveOrderInfo(
            Integer fieldId, String soldSeats, String seatsName, Integer userid) {

        //编号
        String uuid = UUIDUtil.genUuid();

        //影片信息
        FilmInfoVO filmInfoVO = cinemaServiceAPI.getFilmInfoByFieldId(fieldId);
        Integer filmId = Integer.parseInt(filmInfoVO.getFilmId());

        //获取影院信息
        OrderQueryVO orderQueryVO = cinemaServiceAPI.getOrderNeeds(fieldId);
        Integer cinemaId = Integer.parseInt(orderQueryVO.getCinemaId());
        double filmPrice = Double.parseDouble(orderQueryVO.getFilmPrice());

        //求订单总金额
        int solds = soldSeats.split(",").length;
        double totalPrice = getTotalPrice(solds,filmPrice);

        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(uuid);
        moocOrderT.setSeatsName(seatsName);
        moocOrderT.setSeatsIds(soldSeats);
        moocOrderT.setOrderUser(userid);
        moocOrderT.setOrderPrice(totalPrice);
        moocOrderT.setFilmPrice(filmPrice);
        moocOrderT.setFilmId(filmId);
        moocOrderT.setFieldId(fieldId);
        moocOrderT.setCinemaId(cinemaId);

        Integer insert = moocOrderTMapper.insert(moocOrderT);
        if(insert>0){
            //返回查询结果
            OrderVO orderVO = moocOrderTMapper.getOrderInfoById(uuid);
            if(orderVO==null || orderVO.getOrderId()==null){
                log.error("订单信息查询失败，订单编号为{}",uuid);
                return null;
            }else{
                return orderVO;
            }
        }else{
            //插入出错
            log.error("订单插入失败");
            return null;
        }
    }

    private Double getTotalPrice(int solds,double filmprice){
        BigDecimal soldDeci = new BigDecimal(solds);
        BigDecimal filmDeci = new BigDecimal(filmprice);
        BigDecimal result = soldDeci.multiply(filmDeci);

        //四舍五入，取小数点后两位
        BigDecimal bigDecimal = result.setScale(2, RoundingMode.HALF_UP);

        return bigDecimal.doubleValue();
    }

    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId,Page<OrderVO> page) {
        Page<OrderVO> result = new Page<>();
        if(userId==null){
            log.error("订单查询业务失败，用户编号未传入");
            return null;
        }else {
            List<OrderVO> ordersByUserId = moocOrderTMapper.getOrdersByUserId(userId,page);
            if(ordersByUserId==null || ordersByUserId.size()==0){
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            }else{
                //获取订单总数
                EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<MoocOrderT>();
                wrapper.eq("order_user",userId);
                Integer counts = moocOrderTMapper.selectCount(wrapper);
                //将结果放入page
                result.setTotal(counts);
                result.setRecords(ordersByUserId);
                return result;
            }
        }
    }


    //根据放映场次，获取所有已售座位
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {

        if(fieldId==null){
            log.error("查询已售座位错误，未传入任何场次编号");
            return "";
        }else {
            String soldSeatsByFieldId = moocOrderTMapper.getSoldSeatsByFieldId(fieldId);
            return soldSeatsByFieldId;
        }
    }

    @Override
    public OrderVO getOrderInfoById(String orderId) {
        OrderVO orderInfoById = moocOrderTMapper.getOrderInfoById(orderId);
        return orderInfoById;
    }

    @Override
    public boolean paySuccess(String orderId) {
        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(1);
        Integer integer = moocOrderTMapper.updateById(moocOrderT);
        if(integer>=1){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean payFail(String orderId) {
        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(orderId);
        moocOrderT.setOrderStatus(2);
        Integer integer = moocOrderTMapper.updateById(moocOrderT);
        if(integer>=1){
            return true;
        }else {
            return false;
        }
    }
}
