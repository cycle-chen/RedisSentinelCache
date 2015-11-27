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

import com.alibaba.fastjson.JSON;
import com.redis.sentinel.cache.entity.Account;
import com.redis.sentinel.cache.jedis.JedisClient;
import com.redis.sentinel.cache.jedis.JedisPipelinedCallback;
import com.redis.sentinel.cache.jedis.JedisPipelinedCallbackNoResult;

public class MyTest {
	/**
	 * hset读/写基本类型的值----------------------------------------------------------
	 */
	@Test
	public void testWriteString() {// 100万 时间：434186 ~ 441475ms
									// 占内存:117979768 - 1785472 约等于 112.51M
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			jedisClient.hset("account::1s" + i, "id", i);
			jedisClient.hset("account::1s" + i, "name", "supercyc" + i);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	public void testReadString() {// 两个hget运行时间：358177ms ，一个hget运行时间：186437ms
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			jedisClient.hget("account::1s" + i, "id");
			jedisClient.hget("account::1s" + i, "name");
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	/** ----------------------------------------------------------------------- */
	@Test
	public void testWriteJson() {// 100万 时间：210841ms
									// 占内存:398970256-1785368-266777624
									// 约等于124.3660583496094m
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
	public void testReadJson() {// 时间：174683ms
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
	public void testWriteSerializer() { // 100万 时间：217850ms ~ 247745ms
										// 占内存:266777624-1785368 = 264992256 约等于
										// 252.72m
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
	public void testReadSerializer() {// 100w 时间：177474ms
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			jedisClient.hget("yccser3s" + i, "account");
		}
		// System.out.println(jedisClient.hgetJson("cyc1", "account",
		// Account.class));
		// System.out.println(jedisClient.hgetJson("cyc1", "account",
		// Account.class));
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	/**
	 * hget 批量读/写 基本类型 -------------------------------------------
	 */
	@Test
	public void pipelineWriteString() {// 100万 时间：5702 ~ 11533 占内存：111.00M
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		jedisClient.runWithPipeline(new JedisPipelinedCallbackNoResult() {
			@Override
			public void execute(Pipeline pipeline) {
				for (int i = 0; i < 10000000; i++) {
					pipeline.hset("piple2s" + i, "account", "value" + i);
				}
			}
		});
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	public void pipelineReadString() {// 100万 时间：2837 ~ 3085 ~ 3116 ~ 3737
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		List<Object> list = jedisClient
				.runWithPipeline(new JedisPipelinedCallback() {
					@Override
					public List<Object> execute(Pipeline pipeline) {
						for (int i = 0; i < 1000000; i++) {
							pipeline.hget("piple2s" + i, "account");
						}
						return null;
					}
				});
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	/**
	 * ------------------------------------------------------------------------
	 * --
	 */
	/** hget 批量读/写 json对象类型 */
	@Test
	public void pipelineWriteJson() {// 100万 时间：6458ms ~ 6986ms 占内存：127.78M
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		jedisClient.runWithPipeline(new JedisPipelinedCallbackNoResult() {
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
	public void pipelineReadJson() {// 100万 时间：3327 ~ 3439
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		jedisClient.runWithPipeline(new JedisPipelinedCallback() {
			@Override
			public List<Object> execute(Pipeline pipeline) {
				for (int i = 0; i < 1000000; i++) {
					Account account = new Account();
					account.setId(i);
					account.setName("supercyc" + i);
					pipeline.hget("jsonycc2s" + i, "account");

				}
				return null;
			}
		});
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	/** ------------------------------------------------------------------------ */
	/** hget 批量读/写 序列化对象类型 */
	@Test
	public void pipelineWriteSerializer() {// 100万 时间：7558ms ~ 7616 占内存：254.49M
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		final JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		jedisClient.runWithPipeline(new JedisPipelinedCallbackNoResult() {
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

	@Test
	public void pipelineReadSerializer() {// 100万 时间：4052 ~ 7558ms
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		final JedisClient jedisClient = context.getBean(JedisClient.class);
		long start = System.currentTimeMillis();
		jedisClient.runWithPipeline(new JedisPipelinedCallback() {
			@Override
			public List<Object> execute(Pipeline pipeline) {
				for (int i = 0; i < 1000000; i++) {
					Account account = new Account();
					account.setId(i);
					account.setName("supercyc" + i);
					final byte[] rawRegion = jedisClient.rawRegion("pipleser1s"
							+ i);
					final byte[] rawKey = jedisClient.rawKey("account");
					pipeline.hget(rawRegion, rawKey);
				}
				return null;
			}
		});
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}
