package com.litchi.bbs.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Set;

/**
 * author:CuiWJ
 * date:2018/12/9
 */
@Service
public class JedisAdapter {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    @Autowired
    private JedisPool jedisPool;

    /**
     * 从连接池获取redis连接
     *
     * @return Jedis对象
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    //下面对jedis一些方法进行包装，使用连接池连接//

    //Strings相关
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return null;
        }
    }

    public String set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return null;
        }
    }

    public long del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return 0;
        }
    }

    public void setex(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, 10, value);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
        }
    }

    //Sets相关
    public long sadd(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return 0;
        }
    }

    public Set<String> smenbers(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(key);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return null;
        }
    }

    public long srem(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return 0;
        }
    }

    public boolean sismember(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return false;
        }
    }

    public long scard(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return 0;
        }
    }

    //Lists相关
    public long lpush(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return 0;
        }
    }

    public List<String> brpop(int timeout, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return null;
        }
    }

    public List<String> lrange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return null;
        }
    }

    public long llen(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.llen(key);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return 0;
        }
    }

    public String rpop(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpop(key);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return null;
        }
    }

    //对象序列化与反序列化
    public void setObject(String key, Object obj) {
        set(key, JSON.toJSONString(obj));
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value, clazz);
        }
        return null;
    }

    //ZSets相关
    public long zadd(String key, double score, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zadd(key, score, member);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return 0;
        }
    }

    public long zrem(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrem(key, member);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return 0;
        }
    }

    public long zcard(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return 0;
        }
    }

    public Double zscore(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return null;
        }
    }

    public Set<String> zrevrange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return null;
        }
    }

    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            logger.error("创建redis事务失败" + e.getMessage());
        }
        return null;
    }

    /**
     * 执行事务，执行完毕后关闭相应事务和Jedis连接
     *
     * @param transaction
     * @param jedis
     * @return
     */
    public List<Object> exec(Transaction transaction, Jedis jedis) {
        try {
            return transaction.exec();
        } catch (Exception e) {
            logger.error("redis事务执行失败" + e.getMessage());
        } finally {
            if (transaction != null) {
                transaction.close();
            }
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long incr(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(key);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return -1L;
        }
    }
    public long decr(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.decr(key);
        } catch (Exception e) {
            logger.error("Jedis发生异常" + e.getMessage());
            return -1L;
        }
    }
}
