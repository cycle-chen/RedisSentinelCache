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

import redis.clients.jedis.Jedis;

import com.redis.sentinel.cache.entity.Account;
import com.redis.sentinel.cache.jedis.JedisClient;
import com.redis.sentinel.cache.serializer.protostuff.ProtostuffSerializer;

public class SerializerCompare {
	private ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();

	/**
	 * protostuff start
	 */
	@Test
	public void protostuffSerializer() {
		// 1000万 占内存空间：196289800 ~ 187.20M 时间：216848ms
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		Jedis jedis = jedisClient.getResource();
		long start = System.currentTimeMillis();
		for (int id = 1; id <= 1000000; id++) {
			Account account = new Account();
			account.setId(id);
			account.setName("protostuff_ycc" + id);
			byte[] accountbytes = protostuffSerializer.serialize(account);
			byte[] keybytes = protostuffSerializer.serialize("account");
			byte[] fieldbytes = protostuffSerializer
					.serialize("account::" + id);
			jedis.hset(keybytes, fieldbytes, accountbytes);
		}

		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}

	@Test
	public void protostuffDeserializer() {// 时间：181563ms
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		Jedis jedis = jedisClient.getResource();
		long start = System.currentTimeMillis();
		for (int id = 1; id <= 1000000; id++) {
			byte[] keybytes = protostuffSerializer.serialize("account");
			byte[] fieldbytes = protostuffSerializer
					.serialize("account::" + id);
			byte[] accountbytes = jedis.hget(keybytes, fieldbytes);
			Account account = protostuffSerializer.deserialize(accountbytes,
					Account.class);
		}

		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	/**
	 * protostuff end
	 */

}
