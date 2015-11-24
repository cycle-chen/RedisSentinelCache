/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.spring.redis.sentinel.cache.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

@Service
public class EricssonRedisCacheService {
	@Autowired
	private JedisSentinelPool jedisSentinelPool;

	@Test
	public void test() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		EricssonRedisCacheService service = context
				.getBean(EricssonRedisCacheService.class);
		Jedis jedis = service.jedisSentinelPool.getResource();
		// Jedis jedis = jedisSentinelPool.getResource();
		String str = jedis.get("b8");
		System.out.println(str);
	}

}
