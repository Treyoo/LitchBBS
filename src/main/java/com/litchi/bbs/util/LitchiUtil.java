package com.litchi.bbs.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.litchi.bbs.entity.DiscussPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * author:CuiWJ
 * date:2018/12/1
 */
@Component
public class LitchiUtil {
    private static final Logger logger = LoggerFactory.getLogger(LitchiUtil.class);
    public static final int SYSUSER_ID = 6;

    public static String getJSONString(int code, String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        return jsonObject.toJSONString();
    }

    /**
     * 将状态码和map里的键值对转换为json字符串
     *
     * @param code
     * @param map
     * @return
     */
    public static String getJSONString(int code, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toJSONString();
    }

    public static String getJSONString(Object obj){
        return JSON.toJSONString(obj);
    }

    /**
     * 计算MD5
     *
     * @param key 密码
     * @return
     */
    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("生成MD5失败", e);
            return null;
        }
    }

    /**
     * @return 生产随机字符串
     */
    public static String genRandomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T parseObject(String JSONString, Class<T> clazz) {
        return JSON.parseObject(JSONString, clazz);
    }

    public static <T> T parseObject(String JSONString, TypeReference<T> type) {
        return JSON.parseObject(JSONString, type);
    }
}
