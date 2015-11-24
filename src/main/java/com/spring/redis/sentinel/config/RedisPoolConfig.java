package com.spring.redis.sentinel.config;

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
	private @Value("${connectionTimeout}") Integer connectionTimeout;
	private @Value("${soTimeout}") Integer soTimeout;
	private @Value("${password}") String password;
	private @Value("${database}") Integer database;
	private @Value("${password}") String clientName;

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
				sentinels, this, soTimeout, password, database);
		return jedisSentinelPool;
	}

	public String getRedisHost() {
		return redisHost;
	}

	public Integer getRedisPort() {
		return redisPort;
	}

	public String getMaster() {
		return master;
	}

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public Integer getSoTimeout() {
		return soTimeout;
	}

	public String getPassword() {
		return password;
	}

	public Integer getDatabase() {
		return database;
	}

	public String getClientName() {
		return clientName;
	}

}
