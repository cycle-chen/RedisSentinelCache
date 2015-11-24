///*------------------------------------------------------------------------------
// * COPYRIGHT Ericsson 2015
// *
// * The copyright to the computer program(s) herein is the property of
// * Ericsson Inc. The programs may be used and/or copied only with written
// * permission from Ericsson Inc. or in accordance with the terms and
// * conditions stipulated in the agreement/contract under which the
// * program(s) have been supplied.
// *----------------------------------------------------------------------------*/
//package com.spring.redis.cache.manager;
//
//import org.springframework.data.redis.cache.RedisCache;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import com.spring.redis.cache.RedisSetCache;
//
//public class RestSetCacheManager extends RedisCacheManager {
//
//	public RestSetCacheManager(RedisTemplate template) {
//		super(template);
//	}
//
//	@Override
//	@SuppressWarnings("unchecked")
//	protected RedisCache createCache(String cacheName) {
//		long expiration = computeExpiration(cacheName);
//		return new RedisSetCache(cacheName, (isUsePrefix() ? getCachePrefix()
//				.prefix(cacheName) : null), getTemplate(), expiration);
//	}
// }
