package com.stylefeng.guns.rest.modular.cinema;

import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cinema/")
public class CinemaController {

    @RequestMapping(value = "",method = RequestMethod.GET)
    public ResponseVO getCinemas(){

        return null;
    }
}
