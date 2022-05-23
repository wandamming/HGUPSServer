/*

package com.hgups.express.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class RedisUtil {

    private static final String SALE_REDIS_KEY = "sale_conf:";
    private JedisPool jedisPool;

    @Value(value = "${redis.host}")
    private String host;
    @Value(value = "${redis.port}")
    private Integer port;
    @Value(value = "${redis.password}")
    private String password;
    @Value(value = "${redis.timeout}")
    private Integer timeout;
    @Value(value = "${redis.max.connection}")
    private Integer maxConnection;
    @Value(value = "${redis.min.connection}")
    private Integer minConnection;

    //新增属性
    @Value(value = "${redis.testOnCreate}")
    private Boolean testOnCreate;
    @Value(value = "${redis.testOnBorrow}")
    private Boolean testOnBorrow;//在获取连接的时候检查有效性
    @Value(value = "${redis.testOnReturn}")
    private Boolean testOnReturn;//当调用return Object方法时，是否进行有效性检查
    @Value(value = "${redis.testWhileIdle}")
    private Boolean testWhileIdle;//在空闲时检查有效性

    @PostConstruct
    public void init() {
        GenericObjectPoolConfig jedisPoolConfig = new GenericObjectPoolConfig();
        jedisPoolConfig.setMaxTotal(maxConnection);
        jedisPoolConfig.setMaxIdle(minConnection);
        jedisPoolConfig.setMinIdle(minConnection);
        jedisPoolConfig.setMaxWaitMillis(60000);
        jedisPoolConfig.setTestOnCreate(testOnCreate);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);
        jedisPoolConfig.setTestWhileIdle(testWhileIdle);
        jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
    }

    @PreDestroy
    public void close() {
        jedisPool.close();
    }

    public static void main(String[] args) {
        GenericObjectPoolConfig jedisPoolConfig = new GenericObjectPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(5);
        jedisPoolConfig.setMinIdle(5);
        jedisPoolConfig.setMaxWaitMillis(60000);

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379, 60000, "19921218");
        Jedis resource = jedisPool.getResource();
        String key = "ehello";
        resource.set(key, "10");

        Long decr = resource.decr(key);
        System.out.println(decr);
        Long decr2 = resource.decr(key);
        System.out.println(decr2);
        Long decr3 = resource.decr(key);
        System.out.println(decr3);
    }

    public void setSaleNumber(long confId, Integer number) {
        try (Jedis resource = jedisPool.getResource()) {
            resource.set(SALE_REDIS_KEY + confId, number.toString());
        }
    }

    public int getSaleNumber(long confId) {
        try (Jedis resource = jedisPool.getResource()) {
            try {
                return Integer.parseInt(resource.get(SALE_REDIS_KEY + confId));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    public long decr(long confId) {
        try (Jedis resource = jedisPool.getResource()) {
            String key = SALE_REDIS_KEY + confId;
            String remainNumber = resource.get(key);
            if (StringUtils.equalsIgnoreCase("0", remainNumber)) {
                return -1;
            }
            return resource.decr(key);
        }
    }

    public void incr(long confId) {
        try (Jedis resource = jedisPool.getResource()) {
            String key = SALE_REDIS_KEY + confId;
            int remainNumber = Integer.parseInt(resource.get(key));
            if (remainNumber < 0) {
                resource.set(key, "0");
            }
            resource.incr(key);
        }
    }
}

*/
