package com.litchi.bbs.async;

import com.litchi.bbs.entity.DiscussPost;
import com.litchi.bbs.entity.EntityType;
import com.litchi.bbs.entity.Message;
import com.litchi.bbs.service.DiscussPostService;
import com.litchi.bbs.service.ElasticsearchService;
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
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticsearchService elasticsearchService;

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

    @KafkaListener(topics = {TOPIC_PUBLISH_DISCUSS})
    public void handlePublishDiscuss(ConsumerRecord<String, String> record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空！");
            return;
        }
        Event event = LitchiUtil.parseObject(record.value(), Event.class);
        //同步更新到Elasticsearch
        if (EntityType.DISCUSS_POST != event.getEntityType()) {
            logger.error("实体类型不是帖子！");
            return;
        }
        DiscussPost post = discussPostService.selectById(event.getEntityId());
        elasticsearchService.saveDiscuss(post);
    }

    @KafkaListener(topics = {TOPIC_DELETE_DISCUSS})
    public void handleDeleteDiscuss(ConsumerRecord<String, String> record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空！");
            return;
        }
        Event event = LitchiUtil.parseObject(record.value(), Event.class);
        //同步更新到Elasticsearch
        if (EntityType.DISCUSS_POST != event.getEntityType()) {
            logger.error("实体类型不是帖子！");
            return;
        }
        elasticsearchService.deleteDiscussById(event.getEntityId());
    }
}
