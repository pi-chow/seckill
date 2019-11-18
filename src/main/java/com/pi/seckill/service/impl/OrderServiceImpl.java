package com.pi.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.pi.seckill.model.Order;
import com.pi.seckill.model.Stock;
import com.pi.seckill.service.OrderService;
import com.pi.seckill.service.StockService;
import com.pi.seckill.utils.OrderConsumer;
import com.pi.seckill.utils.StockProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author zhouliyu
 * @since 2019-11-12 16:42:18
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final String TOPIC_STOCK = "stock_r1p1";

    @Autowired
    private JdbcTemplate jt;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockProducer stockProducer;

    @Autowired
    private OrderConsumer orderConsumer;

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {

            Thread thread = new Thread(r);
            thread.setName("kafka-order-pool");
            thread.setDaemon(true);
            return thread;
        }
    });


    /**
     * 订单订阅
     * */

    @PostConstruct
    public void init(){

        KafkaConsumer consumer = (KafkaConsumer) orderConsumer.getConsumer();

        consumer.subscribe(Collections.singleton(TOPIC_STOCK));

        //定时推送
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

                if (!records.isEmpty()){

                    for (ConsumerRecord record : records) {

                        log.info("time = {}, topic = {}, partition = {}, key = {}, value = {}",
                                new Date(record.timestamp()), record.topic(), record.partition(), record.key(), record.value());

                        createOrderMapper(JSON.parseObject((String) record.value(), Stock.class));

                    }

                }


            }
        }, 0, 10, TimeUnit.SECONDS);


    }


    @Override
    public String createOrder(int sid) {

        try {

            //检验库存
            Stock stock = stockService.checkStock(sid);

            //扣库存
            stockService.saleOptimisticStock(stock);

            //下单->kafka->response
            if (!createOrderByKafka(stock)){
                throw new RuntimeException("下单失败");
            }

            return "下单成功";


        }catch (RuntimeException e) {

            return e.getMessage();
        }
    }

    /**
     * 订单入库
     * */
    private Integer createOrderMapper(Stock stock) {

        Integer result = -1;

        Order order = new Order();

        order.setSid(stock.getId());

        order.setName(stock.getName());

        order.setThreadId(stock.getThreadId());

        order.setCreateTime(new Date());

        String sql = "INSERT INTO t_order (sid, name, thread_id, create_time) VALUES(?, ?, ?, ?)";

        try {

            result = jt.update(sql, order.getSid(), order.getName(), order.getThreadId(), order.getCreateTime());

        }catch (CannotGetJdbcConnectionException e) {

            log.error("[db-error] " + e.toString(), e);

        }

        return result;
    }

    /**
     *
     * 订单推送
     */
    private boolean createOrderByKafka(Stock stock){

        stock.setThreadId(Thread.currentThread().getId());

        RecordMetadata recordMetadata = stockProducer.syncSend(new ProducerRecord<String, String>(TOPIC_STOCK, JSON.toJSONString(stock)));

        return recordMetadata.hasOffset();
    }


}
