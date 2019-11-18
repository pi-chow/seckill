package com.pi.seckill.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 模拟器-订阅方
 * @author zhouliyu
 * @since 2019-11-06 16:57:26
 */
@Slf4j
@Component
public class OrderConsumer {

    private Consumer<String, String> consumer;

    public OrderConsumer() {

        Properties prop = new Properties();

        //broker 必须指定
        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.40.185:9092");

        //serializer 必须指定
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        //group-id
        prop.put(ConsumerConfig.GROUP_ID_CONFIG, "mock-group");

        //通过心跳检测检测Consumer崩溃 10s
        prop.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);

        //Consumer处理逻辑最大时间 10s
        prop.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 10000);

        //自动提交位移---如有"精确处理一次"需求，通过手动提交
        prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        //自动提交位移的频率 1s
        prop.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        //无位移信息或位移越界时策略
        prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(prop);

    }

    public Consumer<String, String> getConsumer() {
        return consumer;
    }


}
