package com.cloud.check.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.concurrent.Future;

@Component
public class KafkaSender {
    private static final Logger logger = LoggerFactory.getLogger(KafkaSender.class);
    private static KafkaProducer<String,String> producer;
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    @Value("${bootstrap.server}")
    private String bootstrapServer;

    @Value("${retries}")
    private Integer retries;

    @Value("${batch.size}")
    private Integer batchSize;

    @Value("${buffer.memory}")
    private Integer bufferMemory;

    @PostConstruct
    public void init(){
        try {
            Properties properties = new Properties();
            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServer);
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            properties.put("retries", retries);
            properties.put("batch.size", batchSize);
            properties.put("buffer.memory", bufferMemory);
            producer = new KafkaProducer<String, String>(properties);
        }catch (Exception e){
            logger.error("");
        }
    }

    public void sendMessage(String topic,Object message){
        try {
            Future<RecordMetadata> record = producer.send(new ProducerRecord<>(topic,gson.toJson(message)));
            record.get();
        }catch (Exception e){
            logger.error("");
        }
    }
}
