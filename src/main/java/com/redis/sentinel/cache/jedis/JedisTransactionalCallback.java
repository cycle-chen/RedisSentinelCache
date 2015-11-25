package com.redis.sentinel.cache.jedis;

import redis.clients.jedis.Transaction;

/**
 * Callback for Jedis transaction task
 *
 * @author yaochong.chen
 */
public interface JedisTransactionalCallback {
	/**
	 * execute jedis transactional task
	 *
	 * @param tx
	 *            Jedis Transaction
	 */
	public void execute(Transaction tx);
}
