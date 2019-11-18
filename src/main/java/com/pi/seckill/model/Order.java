package com.pi.seckill.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单
 * @author zhouliyu
 * @since 2019-11-12 15:19:10
 */
@Data
public class Order implements Serializable {
    private static final long serialVersionUID = 7142747948696939413L;

    /**
     * 主键ID
     * */
    private Integer id;

    /**
     * 线程ID
     * */
    private Long threadId;

    /**
     * 库存ID
     * */
    private Integer sid;

    /**
     * 商品名称
     * */
    private String name;

    /**
     * 创建时间
     * */
    private Date createTime;

}
