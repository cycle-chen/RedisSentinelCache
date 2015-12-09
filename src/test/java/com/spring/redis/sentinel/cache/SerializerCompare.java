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

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xerial.snappy.Snappy;

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
    public void protostuffSerializerSnappy() {
        //100万 时间：46744ms ~ 47385ms ~ 48355ms 占内存空间： 1643291312 ~  1643291408 ~ 1.53G
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        System.out.println("running the method of protostuffSerilizerSnappy...");
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for (int id = 1; id <= 10000000; id++) {
                    Account account = new Account();
                    account.setId(id);
                    account.setName("protostuff_ycc" + id);
                    byte[] keybytes = "account".getBytes();//protostuffSerializer.serialize("account");
                    byte[] fieldbytes = ("account::" + id).getBytes();// protostuffSerializer.serialize("account::" + id);
                    byte[] accountbytes = protostuffSerializer.serialize(account);
                    try {
                        pipeline.hset(keybytes, fieldbytes, Snappy.compress(accountbytes));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void protostuffDeserializerSnappy() {
        //1000万  时间：51715ms ~ 53406ms ~ 54665ms ~ 56365ms ~ 56603ms ~ 62333ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        System.out.println("running the method of protostuffDeserializerSnappy...");
        long start = System.currentTimeMillis();
        List<Object> list = jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for (int id = 1; id <= 10000000; id++) {
                    pipeline.hget("account".getBytes(), ("account::" + id).getBytes());
                }
                return null;
            }
        });
        for (Object o : list) {
            byte[] accountbyte = (byte[]) o;
            try {
                Account account = protostuffSerializer.deserialize(Snappy.uncompress(accountbyte), Account.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void protostuffSerializer() {
        // 250万 占内存空间：570536960 ~　544.11M 时间：17223ms
        // 1000万　占内存空间：2147315192 ~ 2.00G　时间：72734ms ~ 74994ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        System.out.println("running the method of protostuffSerializer...");
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int id = 1; id <= 10000000; id++) {
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
        //250万 时间：17467ms ~ 17793ms ~ 18173ms
        // 1000万　时间：73885ms ~ 74259ms ~ 76233ms ~ 77276ms ~ 79124ms ~ 80399ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        System.out.println("running the method of protostuffDeserializer ...");
        long start = System.currentTimeMillis();
        List<Object> list = jedisClient.runWithPipeline(new JedisPipelinedCallback() {

            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int id = 1; id <= 10000000; id++) {
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
        // 250万 占内存空间：516297160 ~ 492.38M  时间：12180ms
        // 1000万　占内存空间： 2107293144 ~　1.96G　时间：55715ms ~ 55848ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        final JedisClient jedisClient = context.getBean(JedisClient.class);
        System.out.println("running the method of snappySerializerPipeline...");
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int id = 1; id <= 10000000; id++) {
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
    public void snappyDeserializerPipeline() {
        // 250万  时间：14952ms ~ 18147ms ~ 18240ms
        // 1000万 时间：63204ms ~ 64566ms ~ 65886ms ~ 66134ms ~  68481ms ~ 70234ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        final JedisClient jedisClient = context.getBean(JedisClient.class);
        System.out.println("running the method of snappyDeserializerPipeline...");
        long start = System.currentTimeMillis();
        List<Object> list = jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int id = 1; id <= 10000000; id++) {
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

    public static void main(String[] args) {
        String str = new String();
        for (int i = 0; i < 1026; i++) {
            str += " " + i;
        }
        System.out.println(str);
    }
}
