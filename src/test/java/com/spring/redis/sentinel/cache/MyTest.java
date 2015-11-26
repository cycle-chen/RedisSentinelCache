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

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Pipeline;

import com.alibaba.fastjson.JSON;
import com.redis.sentinel.cache.entity.Account;
import com.redis.sentinel.cache.jedis.JedisClient;
import com.redis.sentinel.cache.jedis.JedisPipelinedCallback;

public class MyTest {
	@Test
	public void testWriteJson() {// 100万 时间：209771ms
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			Account account = new Account();
			account.setId(i);
			account.setName("supercyc" + i);
			jedisClient.hsetJson("cyc1s" + i, "account", account);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	public void testReadJson() {// 时间：87725ms
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			jedisClient.hgetJson("cyc1s" + i, "account", Account.class);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	public void testWriteSerializer() { // 时间：125285ms
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			Account account = new Account();
			account.setId(i);
			account.setName("superycc" + i);
			jedisClient.hset("yccser3s" + i, "account", account);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	public void testReadSerializer() {// 时间：88940ms
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			jedisClient.hget("yccser3s" + i, "account");
		}
		// System.out.println(jedisClient.hgetJson("cyc1", "account",
		// Account.class));
		// System.out.println(jedisClient.hgetJson("cyc1", "account",
		// Account.class));
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	public void pipelineWriteString() {// 100万 时间：5826 ~ 6082ms ~ 6645 ~ 6974 ~
										// 10849
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		jedisClient.runWithPipeline(new JedisPipelinedCallback() {
			@Override
			public void execute(Pipeline pipeline) {
				for (int i = 0; i < 1000000; i++) {
					pipeline.hset("piple2s" + i, "account", "value" + i);
				}
			}
		});
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	public void pipelineWriteJson() {// 100万 时间：5905 ~ 6182 ~ 6507 ~ 6599 ~ 7179
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		jedisClient.runWithPipeline(new JedisPipelinedCallback() {
			@Override
			public void execute(Pipeline pipeline) {
				for (int i = 0; i < 1000000; i++) {
					Account account = new Account();
					account.setId(i);
					account.setName("supercyc" + i);
					pipeline.hset("jsonycc2s" + i, "account",
							JSON.toJSONString(account));
				}
			}
		});
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	public void pipelineWriteSerializer() {// 100万 时间：7368 ~ 7342 ~ 8746 ~
											// 42019ms
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		final JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		jedisClient.runWithPipeline(new JedisPipelinedCallback() {
			@Override
			public void execute(Pipeline pipeline) {
				for (int i = 0; i < 1000000; i++) {
					Account account = new Account();
					account.setId(i);
					account.setName("supercyc" + i);
					final byte[] rawRegion = jedisClient.rawRegion("pipleser1s"
							+ i);
					final byte[] rawKey = jedisClient.rawKey("account");
					final byte[] rawValue = jedisClient.rawValue(account);
					pipeline.hset(rawRegion, rawKey, rawValue);
				}
			}
		});
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}
