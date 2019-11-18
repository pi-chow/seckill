package com.pi.seckill.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 库存
 * @author zhouliyu
 * @since 2019-11-12 15:08:23
 */
@Data
public class Stock implements Serializable{
    private static final long serialVersionUID = -4156846725125406615L;

    /**
     * 自增ID
     * */
    private Integer id;

    /**
     * 名称
     * */
    private String name;

    /**
     * 库存
     * */
    private Integer count;

    /**
     * 线程ID
     * */
    private Long threadId;

    /**
     * 已售
     * */
    private Integer sale;

    /**
     * 版本号-乐观锁
     * */
    private Integer version;
}
