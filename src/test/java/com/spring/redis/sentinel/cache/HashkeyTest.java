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

import redis.clients.jedis.Pipeline;

import com.redis.sentinel.cache.jedis.JedisClient;
import com.redis.sentinel.cache.jedis.JedisPipelinedCallback;

public class HashkeyTest {
    /**
     * long keys
     */
    @Test
    public void longKeyPerformanceTest() {
        // 1000w time:34046
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        List<Object> list = jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 1; i <= 10000000; i++) {
                    pipeline.hset("account::phoneNumber", i + "", "ycc" + i);
                }
                return null;
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("执行时间 :" + (end - start));
    }

    @Test
    public void shortKeyPerformanceTest() {
        // 1000w time:256565 mem:859.32m
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        List<Object> list = jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 1; i <= 10000000; i++) {
                    pipeline.hset("at::pN", i + "", "ycc" + i);
                }
                return null;
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("执行时间 :" + (end - start));
    }
}
