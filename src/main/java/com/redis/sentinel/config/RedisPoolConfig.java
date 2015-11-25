package com.redis.sentinel.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import redis.clients.jedis.JedisSentinelPool;

/**
 *
 */
@Configuration
@PropertySource("classpath:/redis.properties")
public class RedisPoolConfig extends GenericObjectPoolConfig {
	private @Value("${host}") String redisHost;
	private @Value("${port}") Integer redisPort;
	private @Value("${master}") String master;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		return configurer;
	}

	@Bean
	public JedisSentinelPool jedisSentinelPool() {
		Set<String> sentinels = new HashSet<String>();
		sentinels.add(redisHost + ":" + redisPort);
		JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(master,
				sentinels, this);
		return jedisSentinelPool;
	}

}
