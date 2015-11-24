package com.spring.redis.sentinel.config;

import redis.clients.jedis.Jedis;

public class RedisSentinelJedisPool extends RedisSentinel {

	/**
	 * create RedisSentinelJedisPool
	 *
	 * @param host
	 *
	 * @param port
	 *
	 * @param clusterName
	 *
	 */
	public RedisSentinelJedisPool() {
		this.jedisSentinel = new Jedis(poolConfig.getRedisHost(),
				poolConfig.getRedisPort());
		this.createJedisPool(poolConfig.getMaster());
		this.checkRedisSentinelServer(this, 5000, poolConfig.getMaster());
	}

	@Override
	public Object getResource() {
		return jedisPool.getResource();
	}

	@Override
	public void returnBrokenResource(Jedis resource) {
		jedisPool.returnBrokenResource(resource);
	}
}