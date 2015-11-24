//package com.spring.redis.cache;
//
///*------------------------------------------------------------------------------
// * COPYRIGHT Ericsson 2015
// *
// * The copyright to the computer program(s) herein is the property of
// * Ericsson Inc. The programs may be used and/or copied only with written
// * permission from Ericsson Inc. or in accordance with the terms and
// * conditions stipulated in the agreement/contract under which the
// * program(s) have been supplied.
// *----------------------------------------------------------------------------*/
//
//import org.springframework.data.redis.cache.RedisCache;
//import org.springframework.data.redis.cache.RedisCacheElement;
//import org.springframework.data.redis.core.RedisOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.support.collections.DefaultRedisZSet;
//
//public class RedisSetCache extends RedisCache {
//	private RedisTemplate<String, Object> redisTemplate;
//
//	public RedisSetCache(String name, byte[] prefix,
//			RedisTemplate<String, Object> template, long expiration) {
//		super(name, prefix, template, expiration);
//		this.redisTemplate = template;
//		System.out.println(new String(prefix));
//	}
//
//	@Override
//	public void put(RedisCacheElement element) {
//		// super.put(element);
//		element.getKey().getKeyElement();
//		System.out.println(new String(element.getKeyBytes()));
//		RedisOperations<String, Object> op = redisTemplate;
//		DefaultRedisZSet<Object> rset = new DefaultRedisZSet<Object>(
//				new String(element.getKey().getKeyBytes()), op);
//		rset.add(element.get());
//		System.out.println(11);
//	}
// }
