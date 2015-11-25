package com.redis.sentinel.cache.jedis;

import redis.clients.jedis.Jedis;

/**
 * Callback for Jedis task
 *
 * @author yaochong.chen
 */
public interface JedisCallback<T> {
	/**
	 * execute jedis task
	 *
	 * @param jedis
	 *            Jedis instance.
	 * @return return value
	 */
	public T execute(Jedis jedis);
}
