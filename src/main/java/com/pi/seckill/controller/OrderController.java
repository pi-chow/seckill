package com.pi.seckill.controller;

import com.pi.seckill.service.OrderService;
import com.pi.seckill.utils.AccessLimitUtilsRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单
 * @author zhouliyu
 * @since 2019-11-13 10:05:49
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AccessLimitUtilsRedis accessLimitUtilsRedis;

    @GetMapping("/{sid}")
    @ResponseBody
    public String createOrder(@PathVariable int sid){

        try {

            //分布式限流50次/s
            accessLimitUtilsRedis.limit();

        }catch (IllegalArgumentException e) {

            return e.getMessage();
        }



        return orderService.createOrder(sid);

    }

    
}
