package com.pi.seckill.service;

import com.pi.seckill.model.Stock;

/**
 * 库存服务
 * @author zhouliyu
 * @since 2019-11-12 16:36:09
 */
public interface StockService {

    /**
     * 检查库存
     * @param sid 库存ID
     * @return Stock
     * */
    Stock checkStock(int sid);


    /**
     * 扣库存-非线程安全-超买现象
     * @param stock 库存
     * @return  扣库存结果
     * */
    int saleStock(Stock stock);


    /**
     * 扣库存-乐观锁
     * @param stock 库存
     * @return  扣库存结果
     * */
    int saleOptimisticStock(Stock stock);

}
