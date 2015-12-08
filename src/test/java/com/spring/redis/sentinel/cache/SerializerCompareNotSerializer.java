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

import com.redis.sentinel.cache.jedis.JedisPipelinedCallback;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.redis.sentinel.cache.jedis.JedisClient;
import redis.clients.jedis.Pipeline;

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
        // 1000万 时间:59767ms ~ 67268ms 占内存:1483294472 ~ 148333169 ~ 1.38G
        // 一条数据的情况:1000w  time:49014ms memory:1163119160 ~ 1.08G
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        Jedis jedis = jedisClient.getResource();
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 0; i < 10000000; i++) {
                    pipeline.hset("account","id::" + i ,i + "");
//                    Map<String, String> accountMap = new HashMap<String, String>();
//                    accountMap.put("id", i + "");
//                    accountMap.put("name", "supercyc" + i);
//                    pipeline.hmset("account::" + i, accountMap);
                }
                return null;
            }
        });

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void testReadString() {
        // 1000万 时间: 91751ms ~ 93736ms ~ 96759ms ~ 97827ms
        // 只有一条数据的情况：1000w time :49037ms ~ 51231ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        List<Object> list = jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 0; i < 10000000; i++) {
                    pipeline.hget("account","id::" + i);
//                    pipeline.hmget("account::" + i, "id", "name");
                }
                return null;
            }
        });
        for(Object o :list){
            String str = String.valueOf(o);
//            List<String> l = (List<String>)o;
//            l.get(0);
//            l.get(1);
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

        //1000万 时间: 64471ms   占内存:1483294616 ~ 1.38G
        // 只有一条数据的情况：1000w time:
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 0; i <10000000; i++) {
                    pipeline.hset("account","id::" + i,JSON.toJSONString(i));
//                    Map<String, String> accountMap = new HashMap<String, String>();
//                    accountMap.put("id", JSON.toJSONString(i));
//                    accountMap.put("name", JSON.toJSONString("supercyc" + i));
//                    pipeline.hmset("account::" + i, accountMap);
                }
                return null;
            }
        });

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void testReadJson() {

        // 1000w 时间：106732ms ~ 113815ms
        // 单条数据情况：1000w time:52104ms ~
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        List<Object> accountList = jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 0; i < 10000000; i++) {
                    pipeline.hget("account","id::" + i);
                     //pipeline.hmget("account::" + i, "id", "name");
                }
                return null;
            }
        });
        for(Object o:accountList){
            String str = JSON.parseObject(o.toString(),String.class);
//            List<String> l = (List<String>)o;
//            JSON.parseObject(l.get(0), Integer.class);
//            JSON.parseObject(l.get(1), String.class);
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
