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

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.redis.sentinel.cache.jedis.JedisClient;
import com.redis.sentinel.cache.jedis.JedisPipelinedCallback;

/**
 * 使用pipeline与传统的方式的对比 The purpose of this class is ... TODO javadoc
 * for class PipelineCompareNotPipeline
 */
public class PipelineCompareNotPipeline {
    /**
     * 使用pipeline ------------------
     */
    @Test
    public void testWritePipeline() {
        // 1000万 时间:59767ms ~ 67268ms 占内存:1483294472 ~ 148333169 ~ 1.38G
        // 一条数据的情况:1000w  time:49014ms memory:1163119160 ~ 1.08G
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        Jedis jedis = jedisClient.getResource();
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 0; i < 10000000; i++) {
                    pipeline.hset("account", "id::" + i, i + "");
                }
                return null;
            }
        });

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void testReadPipeline() {
        // 1000万 时间: 91751ms ~ 93736ms ~ 96759ms ~ 97827ms
        // 只有一条数据的情况：1000w time :49037ms ~ 51231ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        List<Object> list = jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 0; i < 10000000; i++) {
                    pipeline.hget("account", "id::" + i);
                }
                return null;
            }
        });
        for (Object o : list) {
            String str = String.valueOf(o);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    /**
     * 不使用pipeline
     */
    @Test
    public void testWriteString() {
        // 1000万 时间:59767ms ~ 67268ms 占内存:1483294472 ~ 148333169 ~ 1.38G
        // 一条数据的情况:1000w  time:49014ms memory:1163119160 ~ 1.08G
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            jedisClient.hset("account".getBytes(), ("id::" + i).getBytes(), String.valueOf(i).getBytes());
        }

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
        for (int i = 0; i < 10000000; i++) {
            jedisClient.hget("account".getBytes(), ("id::" + i).getBytes());
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

}
