package com.pi.seckill.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * 模拟器-提供方
 * 用于提供主题类消息推送
 * @author zhouliyu
 * @since 2019-11-06 16:56:26
 */
@Slf4j
@Component
public class StockProducer {

    private Producer<String, String> producer;

    public StockProducer() {

        Properties prop = new Properties();

        //broker 必须指定
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.40.185:9092");

        //serializer 必须指定
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        //0无需响应 1不等待ISR -1 等待ISR
        prop.put(ProducerConfig.ACKS_CONFIG, "1");

        //重试机制：应对leader选举时的波动
        prop.put(ProducerConfig.RETRIES_CONFIG, 1);

        //批量发送同一分区消息量 1M
        prop.put(ProducerConfig.BATCH_SIZE_CONFIG, 1048756);

        //消息缓存区 32MB
        prop.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        //压缩算法 lz4 >> snappy >> gzip
        prop.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

        //消息缓存填满等待
        prop.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 3000);

        //消息发送延时 -》无需关系batch是否填满
        prop.put(ProducerConfig.LINGER_MS_CONFIG, 10);


        producer = new KafkaProducer<>(prop);

    }

    public Producer<String, String> getProducer() {
        return producer;
    }

    /**
     * 同步发送消息
     * */
    public RecordMetadata syncSend(ProducerRecord<String, String> producerRecord) {

        RecordMetadata recordMetadata = null;

        if (producerRecord == null) {
            return null;
        }

        try {
            recordMetadata = producer.send(producerRecord).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }

        return recordMetadata;

    }

    /**
     * 异步发送消息
     * */
    public void asyncSend(ProducerRecord<String, String> producerRecord) {

        if (producerRecord == null) {
            return;
        }


        producer.send(producerRecord, new Callback() {

            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {

                if (e != null) {
                    log.error(e.getMessage());
                }else {
                    log.info("partition = {}, offset = {}, timestamp = {}", recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());
                }
            }
        });


    }
}
