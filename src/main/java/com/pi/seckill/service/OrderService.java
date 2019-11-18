package com.pi.seckill.service;

/**
 * 订单服务
 * @author zhouliyu
 * @since 2019-11-12 16:36:35
 */
public interface OrderService {

    /**
     * 创建订单
     * @param sid 库存ID
     * @return  创建订单结果
     * */
    String createOrder(int sid);



}
