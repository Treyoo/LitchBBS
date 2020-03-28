package com.litchi.bbs.async;

import com.litchi.bbs.entity.Message;
import com.litchi.bbs.service.MessageService;
import com.litchi.bbs.util.LitchiUtil;
import com.litchi.bbs.util.constant.EventTopic;
import com.litchi.bbs.util.constant.LitchiConst;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cuiwj
 * @date 2020/3/27
 */
@Component
public class EventConsumer implements LitchiConst, EventTopic {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleSNSMsg(ConsumerRecord<String, String> record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空！");
            return;
        }
        Event event = LitchiUtil.parseObject(record.value(), Event.class);
        //发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityOwnerId());
        message.setConversationId(event.getTopic());//借用会话id字段存储事件主题
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getActorId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if (!event.getExts().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getExts().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(LitchiUtil.toJSONString(content));//借用内容字段存储通知数据
        messageService.addMessage(message);
    }
}
