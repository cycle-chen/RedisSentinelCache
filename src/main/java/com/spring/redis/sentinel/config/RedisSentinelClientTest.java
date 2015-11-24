package com.spring.redis.sentinel.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;

public class RedisSentinelClientTest {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-config.xml");
		RedisSentinel redisSentinelJedisPool = new RedisSentinelJedisPool();
		setRedisSentinelJedisPool(redisSentinelJedisPool);
	}

	public static void setRedisSentinelJedisPool(final RedisSentinel client) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				while (true) {
					Jedis jedis = null;
					try {
						jedis = (Jedis) client.getResource();
						Thread.sleep(1000);
						jedis.set("b" + count, "b" + count);
						count++;
						System.out.println("set" + count);
					} catch (Exception e) {
						e.printStackTrace();
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					} finally {
						client.returnBrokenResource(jedis);
					}
				}
			}
		}).start();
	}
}