package com.litchi.bbs.config;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis配置类
 * @author CuiWJ
 * date:2018/12/9
 */

@Configuration
@PropertySource(value = {"classpath:redis.properties"})
public class RedisConfig {

    private Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.database}")
    private int database;

    @Value("${spring.redis.password}")
    private String password;

    /**
     * @return
     * @Title: getJedisPool
     * @Description: 获取jedisPool
     */
    @Bean
    public JedisPool getJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        JedisPool pool = new JedisPool(config, host, port, 2000,
                password.equals("") ? null : password, database);
        log.info("初始化Jedis连接池成功.");
        return pool;
    }
}
