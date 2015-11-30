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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Pipeline;
import redis.clients.util.SafeEncoder;

import com.redis.sentinel.cache.jedis.JedisClient;
import com.redis.sentinel.cache.jedis.JedisPipelinedCallback;
import com.redis.sentinel.cache.jedis.JedisPipelinedCallbackNoResult;

public class CustomerAndAccount {
	public static void main(String[] args) {
		System.out.println("customer::userid：：u" + (2 - 1));
		System.out.println(SafeEncoder.encode("userid::u1"));
	}

	@Test
	public void testhashlistwrite() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		jedisClient.runWithPipeline(new JedisPipelinedCallbackNoResult() {
			@Override
			public void execute(Pipeline pipeline) {
				for (int id = 1; id <= 1000000; id++) {
					pipeline.hset("customer::" + id, "CUSTOMERNUMBER",
							String.valueOf(id));
					if ((id % 2) == 0) {
						pipeline.hset("customer::" + id, "userid", "u"
								+ (id - 1));
						pipeline.sadd("customer::userid：：u" + (id - 1),
								String.valueOf(id));
					} else {
						pipeline.hset("customer::" + id, "userid", "u" + id);
						pipeline.sadd("customer::userid：：u" + id,
								String.valueOf(id));
					}
				}
			}
		});

		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	public void testhashlistread() {// 读取100万条数据 时间为：22635ms
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		final List<Object> useridList = jedisClient
				.runWithPipeline(new JedisPipelinedCallback() {
					@Override
					public List<Object> execute(Pipeline pipeline) {
						for (int id = 1; id <= 1000000; id++) {

							pipeline.smembers("customer::userid：：u" + id);
						}
						return null;
					}
				});
		List<Object> customers = jedisClient
				.runWithPipeline(new JedisPipelinedCallback() {
					@Override
					public List<Object> execute(Pipeline pipeline) {
						for (Object userid : useridList) {
							HashSet<String> hs = (HashSet<String>) userid;
							Iterator<String> itors = hs.iterator();
							while (itors.hasNext()) {
								String customernumber = itors.next();
								pipeline.hmget("customer::" + customernumber,
										"CUSTOMERNUMBER", "userid");
							}
						}
						return null;
					}
				});
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println(customers.size());
	}
}
