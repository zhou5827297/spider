package com.spider.config;

import com.spider.util.PropertiesUtils;

import java.util.Properties;

public class RedisConstant {
    private final static Properties PROPERTIES = PropertiesUtils.loadProperties("conf/redis.properties");

    public final static String HOST = getString("redis.host");

    public final static int PORT = getInt("redis.port");

    public final static String PASSWORD = getString("redis.password");

    public final static int TIMEOUT = getInt("redis.timeout");

    public final static int DATABASE = getInt("redis.default.db");

    public final static int POOLMAXACTIVE = getInt("redis.pool.maxActive");

    public final static int POOLMAXIDLE = getInt("redis.pool.maxIdle");

    public final static int POOLMAXWAIT = getInt("redis.pool.maxWait");

    public final static boolean POOLTESTONBORROW = getBoolean("redis.pool.testOnBorrow");

    public final static int REDIS_PULL_SWITCH = getInt("redis.pull.switch");

    private final static String getString(String key) {
        return PROPERTIES.getProperty(key);
    }

    private final static int getInt(String key) {
        return Integer.parseInt(PROPERTIES.getProperty(key));
    }

    private final static boolean getBoolean(String key) {
        return Boolean.parseBoolean(PROPERTIES.getProperty(key));
    }
}
