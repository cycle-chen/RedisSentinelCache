//package com.spring.redis;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.StringTokenizer;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import redis.clients.jedis.JedisPoolConfig;
//import redis.clients.jedis.JedisSentinelPool;
//
//@Configuration
//public class RedisConfiguration {
//
//	@Value("${redis.hostname}")
//	String hostname;
//	@Value("${redis.password}")
//	String password;
//	@Value("${redis.port}")
//	int port;
//
//	@Value("${redis.pool.maxTotal}")
//	int maxTotal;
//	@Value("${redis.pool.maxIdle}")
//	int maxIdle;
//	@Value("${redis.pool.maxWait}")
//	int maxWait;
//
//	@Value("${redis.sentinel.master}")
//	String master;
//	@Value("${redis.sentinel.nodes}")
//	String nodes;
//	@Value("${morning.is_product}")
//	boolean isProduct;
//
//	@Bean
//	public JedisConnectionFactory redisConnectionFactory() {
//
//		if (isProduct) {
//			Set<String> sentinels = new HashSet<String>();
//			sentinels = this.stringConvertToSet(nodes);
//			JedisSentinelPool pool = new JedisSentinelPool(master,sentinels);
//			JedisPoolConfig poolConfig = new JedisPoolConfig();
//			pool.initPool(poolConfig, null);
//			poolConfig.
//			return new JedisConnectionFactory(sentinelConfig);
//		} else {
//			JedisPoolConfig poolConfig = new JedisPoolConfig();
//			poolConfig.setMaxIdle(maxIdle);
//			poolConfig.setMaxWaitMillis(maxWait);
//			poolConfig.setMaxTotal(maxTotal);
//
//			JedisConnectionFactory jcf = new JedisConnectionFactory(poolConfig);
//			jcf.setHostName(hostname);
//			jcf.setPort(port);
//			jcf.setPassword(password);
//			jcf.setPoolConfig(poolConfig);
//
//			return jcf;
//		}
//	}
//
//	@Bean
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public RedisTemplate redisTemplate() {
//
//		RedisTemplate redisTemplate = new RedisTemplate();
//		redisTemplate.setConnectionFactory(redisConnectionFactory());
//
//		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//		RedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();
//
//		redisTemplate.setKeySerializer(stringRedisSerializer);
//		redisTemplate.setValueSerializer(jdkSerializer);
//
//		return redisTemplate;
//	}
//
//	public Set<String> stringConvertToSet(String source) {
//		Set<String> target = new HashSet<String>();
//		StringTokenizer st = new StringTokenizer(source, ",");
//		while (st.hasMoreElements()) {
//			target.add(st.nextElement().toString());
//		}
//		return target;
//	}
//
// }
