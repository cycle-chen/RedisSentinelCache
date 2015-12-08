package com.spring.redis.sentinel.cache;

import com.alibaba.fastjson.JSON;
import com.redis.sentinel.cache.entity.Account;
import com.redis.sentinel.cache.jedis.JedisClient;
import com.redis.sentinel.cache.jedis.JedisPipelinedCallback;
import com.sun.glass.ui.Application;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.Pipeline;

import java.util.List;

/**
 * Created by EYAOCCH on 12/8/2015.
 * 这里假设Account类中，phoneNumber唯一，但name与age可以重复。
 */
public class HashSetIndexTest {
    /**
     * hash index test
      */
    @Test
    public void hashIndexTest(){
        //1000w time:104790ms mem:2484126608 ~ 2.31G
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for(int i = 1 ;i <= 10000000;i++){
                    Account account = new Account();
                    account.setAge(17 + i%2);
                    account.setId(1 + i);
                    account.setName("ycc" + i);
                    account.setPhoneNumber(i);
                    //将原数据存入Hash中
                    pipeline.hset("account",i + "", JSON.toJSONString(account));
                    //将搜索条件phoneNumber存入hash中，phoneNumber唯一
                    pipeline.hset("account::phoneNumber",i + "",account.getId() + "");
                    //将搜索条件age存入set中，age可能重复
                    //pipeline.sadd("account::age::" + account.getAge(),account.getId() + "");
                    //将搜索条件name存入set中，name可能重复。
                    //pipeline.sadd("account::name::" + account.getName(),account.getId() + "");
                }
                return null;
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("执行时间 :" + (end-start));
    }
    @Test
    public void hashIndexSearchTest(){
        // 1000w time:45798ms ~ 47299ms ~ 48144ms
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        List<Object> list = jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for(int i = 1 ;i <= 10000000;i++){
                    pipeline.hget("account::phoneNumber",i + "");
                }
                return null;
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("执行时间 :" + (end-start));
    }

    /**
     * set index test
     */
    @Test
    public void setIndexTest(){
        //1000w time: 98737ms  mem:2897337752 ~ 2.70G
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for(int i = 1 ;i <= 10000000;i++){
                    Account account = new Account();
                    account.setId(1 + i);
                    account.setName("ycc" + i);
                    account.setAge(17 + i%2);
                    account.setPhoneNumber(i);
                    //将原数据存入Hash中
                    pipeline.hset("account",i + "", JSON.toJSONString(account));
                    //将搜索条件phoneNumber存入set中，phoneNumber唯一
                    pipeline.sadd("account::phoneNumber::" + account.getPhoneNumber(),account.getId() + "");
                    //将搜索条件age存入set中，age可能重复
                    //pipeline.sadd("account::age::" + account.getAge(),account.getId() + "");
                    //将搜索条件name存入set中，name可能重复。
                    //pipeline.sadd("account::name::" + account.getName(),account.getId() + "");
                }
                return null;
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("执行时间 :" + (end-start));
    }
    @Test
    public void setIndexSearchTest(){
        //1000w time:83529
        //900w time:67664
        //800w time :59697
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        List<Object> list = jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            public List<Object> execute(Pipeline pipeline) {
                for(int i = 1;i <=10000000 ;i++){
                   pipeline.smembers("account::phoneNumber::" + i);
                }
                return null;
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("执行时间:" + (end -start));
        for(Object o:list){
            System.out.println(o);
            break;
        }
    }
}
