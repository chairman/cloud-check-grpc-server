package com.cloud.check.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Properties;

@Component
public class KafkaRevicer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaRevicer.class);

    @Value("${bootstrap.server}")
    private String bootstrapServer;

    @Value("${enable.auto.commit}")
    private Boolean enableAutoCommit;

    @Value("${auto.offset.rest}")
    private String autoOffsetReset;

    @Value("${group.id}")
    private String groupId;

    @PostConstruct
    public void handle(){
        RunableKafkaRevicer runableKafkaRevicer = new RunableKafkaRevicer();
        new Thread(runableKafkaRevicer,"runableKafkaRevicer").start();
    }

    class RunableKafkaRevicer implements Runnable{

        @Override
        public void run() {
            Properties properties = new Properties();
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServer);
            if(enableAutoCommit!=null) properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,enableAutoCommit.toString());
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,autoOffsetReset);
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.put(ConsumerConfig.GROUP_ID_CONFIG,groupId);
            properties.put(ConsumerConfig.CLIENT_ID_CONFIG,groupId);
            KafkaConsumer<String,String> consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(Arrays.asList("test_list1"));

            while (true){
                ConsumerRecords<String,String> records = consumer.poll(100);
                for (ConsumerRecord<String,String> record:records){
                    logger.info("这就是数据 key:{},value:{}",record.key(),record.value());
                }
            }
        }
    }
}
