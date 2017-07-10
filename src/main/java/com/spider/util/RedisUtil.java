package com.spider.util;

import com.spider.config.RedisConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

import java.io.*;
import java.util.Set;


public class RedisUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    private static RedisUtil INSTANCE;

    private JedisPool pool;

    private JedisShardInfo shardInfo;

    private final static ThreadLocal<Jedis> JREDIS_THREAD = new ThreadLocal<>();

    private RedisUtil() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(RedisConstant.POOLMAXACTIVE);
        jedisPoolConfig.setMaxIdle(RedisConstant.POOLMAXIDLE);
        jedisPoolConfig.setMaxWaitMillis(RedisConstant.POOLMAXWAIT);
        jedisPoolConfig.setTestOnBorrow(RedisConstant.POOLTESTONBORROW);
        shardInfo = new JedisShardInfo(RedisConstant.HOST, RedisConstant.PORT, RedisConstant.TIMEOUT);
        if (StringUtils.isNotBlank(RedisConstant.PASSWORD)) {
            shardInfo.setPassword(RedisConstant.PASSWORD);
        }
        pool = new JedisPool(jedisPoolConfig, shardInfo.getHost(), shardInfo.getPort(), shardInfo.getTimeout(), shardInfo.getPassword(), RedisConstant.DATABASE);
    }

    public static synchronized RedisUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RedisUtil();
        }
        return INSTANCE;
    }

    public Jedis getJedis() {
        Jedis jedis = pool.getResource();
        JREDIS_THREAD.set(jedis);
        return jedis;
    }

    public void returnJedis() {
        Jedis jedis = JREDIS_THREAD.get();
        if (jedis != null) {
            jedis.close();
        }
        JREDIS_THREAD.remove();
    }

    public void returnJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public void setex(String key, int seconds, String value) {
        Jedis jedis = pool.getResource();
        try {
            jedis.setex(key, seconds, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void setex(String key, int seconds, Object value) {
        Jedis jedis = pool.getResource();
        try {
            jedis.setex(key.getBytes(), seconds, toBytes(value));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean exists(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.exists(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, Object obj) {
        Jedis jedis = pool.getResource();
        try {
            jedis.set(key.getBytes(), toBytes(obj));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Object get(String key) {
        Jedis jedis = pool.getResource();
        try {
            return toObject(jedis.get(key.getBytes()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean hSet(String key, String field, Object obj) {
        Jedis jedis = pool.getResource();
        try {
            jedis.hset(key.getBytes(), field.getBytes(), toBytes(obj));
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Object hGet(String key, String field) {
        Jedis jedis = pool.getResource();
        try {
            return toObject(jedis.hget(key.getBytes(), field.getBytes()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String hGetStr(String key, String field) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.hget(key, field);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public Long hDel(String key, String field) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.hdel(key, field);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public long hIncrby(String key, String field, long value) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.hincrBy(key, field, value);
        } catch (Throwable e) {
            return 0;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public Set<String> keys(String startStr) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.keys(startStr);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public Set<String> hKeys(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.hkeys(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void del(String key) {
        Jedis jedis = pool.getResource();
        try {
            if (jedis.exists(key.getBytes())) {
                jedis.del(key.getBytes());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void rpush(String key, String value) {
        Jedis jedis = pool.getResource();
        try {
            jedis.rpush(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public String lpop(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.lpop(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }


    public void rename(String oldKey, String newKey) {
        Jedis jedis = pool.getResource();
        try {
            if (jedis.exists(oldKey.getBytes())) {
                jedis.rename(oldKey.getBytes(), newKey.getBytes());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void replace(String key, String tempKey) {
        del(key);
        rename(tempKey, key);
    }

    public void close() {
        INSTANCE = null;
        shardInfo = null;
        pool.destroy();
        pool = null;
    }

    private byte[] toBytes(Object obj) throws IOException {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            return bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
    }

    private Object toObject(byte[] bytes) throws Exception {
        ByteArrayInputStream bos = null;
        ObjectInputStream oos = null;
        try {
            Object obj = null;
            if (bytes != null) {
                bos = new ByteArrayInputStream(bytes);
                oos = new ObjectInputStream(bos);
                obj = oos.readObject();
            }
            return obj;
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
    }

}
