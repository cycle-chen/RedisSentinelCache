package com.redis.sentinel.cache.jedis;

import java.util.List;

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
	List<Object> execute(final Pipeline pipeline);

}
