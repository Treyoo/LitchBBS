package com.litchi.bbs.async;

import com.litchi.bbs.util.LitchiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author cuiwj
 * @date 2020/3/27
 */
@Component
public class EventProducer {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    // 发布事件
    public void fireEvent(Event event) {
        kafkaTemplate.send(event.getTopic(), LitchiUtil.toJSONString(event));
    }
}
