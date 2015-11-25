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

import com.redis.sentinel.cache.entity.Account;
import com.redis.sentinel.cache.jedis.JedisClient;

public class My {
	@Test
	public void test() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		JedisClient jedisClient = context.getBean(JedisClient.class);
		Account account = new Account();
		account.setId(1);
		account.setName("supercyc");
		jedisClient.hsetJson("cyc1", "name", account);
		System.out.println(jedisClient.hgetJson("cyc1", "name", Account.class));
	}
}
