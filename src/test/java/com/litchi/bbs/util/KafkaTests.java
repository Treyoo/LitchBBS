package com.litchi.bbs.util;

import com.litchi.bbs.BbsApplication;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author cuiwj
 * @date 2020/3/27
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BbsApplication.class)
public class KafkaTests {
    private static final Logger logger = LoggerFactory.getLogger(KafkaTests.class);
    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka() throws InterruptedException {
        logger.warn("注意本测试需要先启动Kafka Server!");
        kafkaProducer.sendMessage("test","测试Kafka发送消息。");
        kafkaProducer.sendMessage("test","Hello World!");
        Thread.sleep(10*1000);
    }
}

@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic,content);
    }
}

@Component
class KafkaConsumer{
    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord<String,String> consumerRecord){
        //do something
        System.out.println(consumerRecord.value());
    }
}