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

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Pipeline;

import com.redis.sentinel.cache.jedis.JedisClient;
import com.redis.sentinel.cache.jedis.JedisPipelinedCallback;

public class HashAndMultiHashCompare {
    private static JedisClient jedisClient;

    @BeforeClass
    public static void beforeClass() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        jedisClient = context.getBean(JedisClient.class);
    }

    @Test
    public void hashStore() {
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 0; i < 10000000; i++) {
                    pipeline.hset("account".getBytes(), (i + "").getBytes(), (i + "").getBytes());
                }
                return null;
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("hashStore执行时间：" + (end - start));
    }

    @Test
    public void hashQuery() {
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 0; i < 10000000; i++) {
                    pipeline.hget("account".getBytes(), (i + "").getBytes());
                }
                return null;
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("hashQuery执行时间：" + (end - start));
    }

    /**
     * multiHash
     */
    @Test
    public void multiHashStore() {
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 0; i < 10000000; i++) {
                    pipeline.hset("account:i".getBytes(), (i + "").getBytes(), (i + "").getBytes());
                }
                return null;
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("multiHashStore执行时间：" + (end - start));
    }

    @Test
    public void multiHashQuery() {
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 0; i < 10000000; i++) {
                    pipeline.hget("account:i:i".getBytes(), (i + "").getBytes());
                }
                return null;
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("multiHashQuery执行时间：" + (end - start));
    }
}
