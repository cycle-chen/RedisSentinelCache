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

import com.redis.sentinel.cache.entity.Account;
import com.redis.sentinel.cache.jedis.JedisClient;
import com.redis.sentinel.cache.jedis.JedisPipelinedCallback;
import com.redis.sentinel.cache.serializer.protostuff.ProtostuffSerializer;

/**
 * 序列化的比较
 */
public class SerializerCompare {
    private ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();

    /**
     * protostuff start
     */
    @Test
    public void protostuffSerializer() {
        // 250万 占内存空间：505271616 ~ 481.86M  时间：23739ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int id = 1; id <= 2500000; id++) {
                    Account account = new Account();
                    account.setId(id);
                    account.setName("protostuff_ycc" + id);
                    byte[] keybytes = protostuffSerializer.serialize("account");
                    byte[] fieldbytes = protostuffSerializer.serialize("account::" + id);
                    byte[] accountbytes = protostuffSerializer.serialize(account);
                    pipeline.hset(keybytes, fieldbytes, accountbytes);
                }
                return null;
            }
        });

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void protostuffDeserializer() {
        //250万 时间：15516ms ~ 15934ms ~ 17314ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        List<Object> list = jedisClient.runWithPipeline(new JedisPipelinedCallback() {

            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int id = 1; id <= 2500000; id++) {
                    byte[] keybytes = protostuffSerializer.serialize("account");
                    byte[] fieldbytes = protostuffSerializer.serialize("account::" + id);
                    pipeline.hget(keybytes, fieldbytes);
                }
                return null;
            }
        });
        for (Object o : list) {
            byte[] accountbytes = (byte[]) o;
            Account account = protostuffSerializer.deserialize(accountbytes, Account.class);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    /**
     * protostuff end
     */

    /**
     * snappySerializer start
     */
    @Test
    public void snappySerializerPipeline() {
        // 250万 占内存空间：451073952 ~ 430.18M  时间：19752ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        final JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int id = 1; id <= 2500000; id++) {
                    Account account = new Account();
                    account.setId(id);
                    account.setName("protostuff_ycc" + id);
                    pipeline.hset(jedisClient.rawRegion("account"), jedisClient.rawKey("account::" + id),
                            jedisClient.rawValue(account));
                }
                return null;
            }
        });

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void snappyDesrializerPipeline() {
        // 250万  时间：15261ms ~ 15686ms ~ 17030ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        final JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        List<Object> list = jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int id = 1; id <= 2500000; id++) {
                    pipeline.hget(jedisClient.rawRegion("account"), jedisClient.rawKey("account::" + id));
                }
                return null;
            }
        });

        for (Object o : list) {
            byte[] bytes = (byte[]) o;
            Account account = (Account) jedisClient.deserializeValue(bytes);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
