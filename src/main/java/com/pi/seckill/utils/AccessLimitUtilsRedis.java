package com.pi.seckill.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 限流操作 50次/s
 * @author zhouliyu
 * @since 2019-11-14 19:13:30
 */
@Slf4j
@Component
public class AccessLimitUtilsRedis {

    /**
     * 限速KEY
     * */
    private final String LIMIT_KEY = "limit";

    /**
     * 时间窗口1s
     * */
    private final Integer PERIOD = 1;

    /**
     * 限流50次
     * */
    private final Integer LIMIT = 50;

    @Autowired
    private StringRedisTemplate redisTemplate;


    public void limit(){

        long currentTime = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(LIMIT_KEY, String.valueOf(currentTime), currentTime);

        redisTemplate.opsForZSet().removeRangeByScore(LIMIT_KEY, 0, currentTime-PERIOD*1000);

        long count = redisTemplate.opsForZSet().zCard(LIMIT_KEY);

        redisTemplate.expire(LIMIT_KEY, PERIOD, TimeUnit.SECONDS);

        if (count > LIMIT) {

            log.info("访问人数过多,请稍后");

            throw new IllegalArgumentException("访问人数过多,请稍后");
        }
    }
}
