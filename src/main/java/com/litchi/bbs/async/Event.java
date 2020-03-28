package com.litchi.bbs.async;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CuiWJ
 * Created on 2018/12/11
 */
public class Event {
    private String topic;//事件类型
    private int actorId;//事件发起者id
    private int entityType;//事件操作实体类型
    private int entityId;//事件操作实体id
    private int entityOwnerId;//实体拥有者id
    private Map<String, Object> exts = new HashMap<>();//扩展,用于保存事件现场信息

    //构造方法
    public Event() {
    }

    public Event(String topic) {
        this.topic = topic;
    }

    //Getter与Setter
    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public Event setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public Event setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, Object> getExts() {
        return exts;
    }

    public Event setExts(Map<String, Object> exts) {
        this.exts = exts;
        return this;
    }

    //一些help方法
    public Object getExt(String key) {
        return exts.get(key);
    }

    public Event setExt(String key, Object value) {
        exts.put(key, value);
        return this;
    }

}
