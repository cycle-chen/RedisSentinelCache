/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.spring.redis.sentinel.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.redis.sentinel.cache.jedis.JedisClient;

/**
 * 存储基本数据类型的几种方式的比较：不做处理的数据、序列化的数据、Json处理的数据
 * 经简单的测试：json在处理基本数据时，执行速度与占内存都是最少的
 */
public class SerializerCompareNotSerializer {
    /**
     * 不对数据做任何的处理
     * hset读/写基本类型的值----------------------------------------
     * ------------------
     */
    @Test
    public void testWriteString() {
        // 100万 时间：251343 ~ 256948ms 占内存:118040976 ~ 118060952约等于 112.51M ~ 112.59M
        // 250万 时间：645674ms 占内存：298645032 约等于 284.81M
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        Jedis jedis = jedisClient.getResource();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2500000; i++) {
            Map<String, String> accountMap = new HashMap<String, String>();
            accountMap.put("id", i + "");
            accountMap.put("name", "supercyc" + i);
            jedis.hmset("account::1s" + i, accountMap);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void testReadString() {
        // 100w 时间：182676ms ~ 183291ms
        // 250w 时间：466990 ~ 469118ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        Jedis jedis = jedisClient.getResource();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2500000; i++) {
            jedis.hmget("account::1s" + i, "id", "name");
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    /**
     * 对数据做Json处理
     * hset读/写基本类型的值----------------------------------------
     * ------------------
     */

    @Test
    public void testWriteJson() {
        // 100万 时间：235098ms ~ 241733ms 占内存:118040440 ~ 118060720 约等于 112.57M ~ 112.59M
        // 250万 时间：604366ms 占内存：298623536 约等于 284.79M
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        Jedis jedis = jedisClient.getResource();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2500000; i++) {
            Map<String, String> accountMap = new HashMap<String, String>();
            accountMap.put("id", JSON.toJSONString(i));
            accountMap.put("name", JSON.toJSONString("supercyc" + i));
            jedis.hmset("account::1s" + i, accountMap);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void testReadJson() {
        // 100w 时间：188011 ~ 184568ms ~ 220049ms
        // 250w 时间：459355ms ~ 456234ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        Jedis jedis = jedisClient.getResource();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2500000; i++) {
            List<String> accountList = jedis.hmget("account::1s" + i, "id", "name");
            JSON.parseObject(accountList.get(0), Integer.class);
            JSON.parseObject(accountList.get(1), String.class);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    /**
     * 序列化数据
     * hset读/写基本类型的值----------------------------------------------
     * ------------
     */

    @Test
    public void testWriteSerializer() {
        // 100万 时间：  占内存:  约等于
        // 250万 时间：713716ms 占内存：   338120336 约等于322.46M
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        Jedis jedis = jedisClient.getResource();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2500000; i++) {
            Map<byte[], byte[]> accountMap = new HashMap<byte[], byte[]>();

            accountMap.put(jedisClient.rawKey("id"), jedisClient.rawValue(i));
            accountMap.put(jedisClient.rawKey("name"), jedisClient.rawValue("supercyc" + i));
            jedis.hmset(jedisClient.rawRegion("account::1s" + i), accountMap);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void testReadSerializer() {
        // 100w 时间：
        // 250w 时间：471145
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        Jedis jedis = jedisClient.getResource();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2500000; i++) {
            List<byte[]> accountList = jedis.hmget(("account::1s" + i).getBytes(), "id".getBytes(), "name".getBytes());
            jedisClient.deserializeValue(accountList.get(0));
            jedisClient.deserializeValue(accountList.get(1));
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
