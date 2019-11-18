package com.pi.seckill.service.impl;

import com.pi.seckill.model.Stock;
import com.pi.seckill.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author zhouliyu
 * @since 2019-11-12 16:42:06
 */
@Slf4j
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private JdbcTemplate jt;


    @Override
    public Stock checkStock(int sid) {

        Stock stock = null;

        String sql = "SELECT * FROM t_stock WHERE id = ?";

        try {

            stock = jt.queryForObject(sql, new Object[]{sid}, new BeanPropertyRowMapper<>(Stock.class));

            if(stock.getSale().equals(stock.getCount())) {

                log.warn("库存不足");
                throw new RuntimeException("库存不足");
            }

        }catch (EmptyResultDataAccessException e) {

            return null;

        } catch (CannotGetJdbcConnectionException e) {

            log.error("[db-error] " + e.toString(), e);

        }

        return stock;
    }

    @Override
    public int saleStock(Stock stock) {

        int result = -1;

        String sql = "UPDATE t_stock SET sale = sale + 1 WHERE id = ? ";

        try {

            result = jt.update(sql, stock.getId());

        }catch (CannotGetJdbcConnectionException e) {

            log.error("[db-error] " + e.toString(), e);

        }


        return result;
    }


    @Override
    public int saleOptimisticStock(Stock stock) {

        int result = 0;

        //通过version防止版本号小的多次更新<乐观锁>

        String sql = "UPDATE t_stock SET sale = sale + 1, version = version + 1 WHERE id = ? AND version = ?";

        try {

            result = jt.update(sql, stock.getId(), stock.getVersion());

            if (result == 0) {

                log.warn("库存不足");

                throw new RuntimeException("库存不足");
            }

        }catch (CannotGetJdbcConnectionException e) {

            log.error("[db-error] " + e.toString(), e);

        }


        return result;
    }


}
