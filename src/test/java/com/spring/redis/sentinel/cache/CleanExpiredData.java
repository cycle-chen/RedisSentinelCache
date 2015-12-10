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

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Pipeline;

import com.alibaba.fastjson.JSON;
import com.redis.sentinel.cache.entity.Account;
import com.redis.sentinel.cache.jedis.JedisClient;
import com.redis.sentinel.cache.jedis.JedisPipelinedCallback;

/**
 * use lua timer to clean the expired datas
 */
public class CleanExpiredData {

    @Test
    public void generateTestData() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        JedisClient jedisClient = context.getBean(JedisClient.class);
        long start = System.currentTimeMillis();
        jedisClient.runWithPipeline(new JedisPipelinedCallback() {
            @Override
            public List<Object> execute(Pipeline pipeline) {
                for (int i = 1; i <= 1000000; i++) {
                    Account account = new Account();
                    account.setAge(17 + (i % 2));
                    account.setId(1 + i);
                    account.setName("ycc" + i);
                    account.setPhoneNumber(i);
                    //将原数据存入Hash中
                    pipeline.hset("account", i + "", JSON.toJSONString(account));
                    //将搜索条件phoneNumber存入hash中，phoneNumber唯一
                    pipeline.hset("account::pN", i + "", account.getId() + "");
                    pipeline.zadd("account:exp", System.currentTimeMillis() + (((i % 10) + 1) * 60 * 1000),
                            account.getId() + "");
                    //将搜索条件age存入set中，age可能重复
                    //pipeline.sadd("account::age::" + account.getAge(),account.getId() + "");
                    //将搜索条件name存入set中，name可能重复。
                    //pipeline.sadd("account::name::" + account.getName(),account.getId() + "");
                }
                return null;
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("执行时间 :" + (end - start));
    }

    @Test
    public void luaTimerCleanexpiredData() {

    }
}
