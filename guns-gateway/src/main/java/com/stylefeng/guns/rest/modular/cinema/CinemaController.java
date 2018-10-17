package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.cinema.CinemaServiceAPI;
import com.stylefeng.guns.api.cinema.vo.CinemaQueryVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cinema/")
public class CinemaController {

    @Reference(interfaceClass = CinemaServiceAPI.class)
    private CinemaServiceAPI cinemaServiceAPI;

    @RequestMapping(value = "getCinemas",method = RequestMethod.GET)
    public ResponseVO getCinemas(CinemaQueryVO cinemaQueryVO){

        return null;
    }

    @RequestMapping(value = "getCondition",method = RequestMethod.GET)
    public ResponseVO getCondition(CinemaQueryVO cinemaQueryVO){

        return null;
    }

    @RequestMapping(value = "getFields",method = RequestMethod.GET)
    public ResponseVO getFields(Integer cinemaId){

        return null;
    }

    @RequestMapping(value = "getFieldInfo",method = RequestMethod.POST)
    public ResponseVO getFieldInfo(Integer cinemaId,Integer fieldId){

        return null;
    }
}
