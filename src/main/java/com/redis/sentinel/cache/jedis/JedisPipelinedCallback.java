package com.redis.sentinel.cache.jedis;

import redis.clients.jedis.Pipeline;

/**
 * Execute method using Redis {@link redis.clients.jedis.Pipeline}
 *
 * @author yaochong.chen
 */
public interface JedisPipelinedCallback {

	/**
	 * execute using Redis Pipeline.
	 *
	 * @param pipeline
	 *            Jedis Pipeline
	 */
	void execute(final Pipeline pipeline);
}
