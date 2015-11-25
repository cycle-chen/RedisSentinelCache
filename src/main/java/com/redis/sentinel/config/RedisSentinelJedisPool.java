package com.redis.sentinel.config;

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
	public RedisSentinelJedisPool(String host, Integer port,
			String... masterName) {
		this.jedisSentinel = new Jedis(host, port);
		this.createJedisPool(masterName);
		this.checkRedisSentinelServer(this, 5000, masterName);
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