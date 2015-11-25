/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.redis.sentinel.cache.jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Transaction;

import com.alibaba.fastjson.JSON;
import com.redis.sentinel.cache.serializer.RedisSerializer;
import com.redis.sentinel.cache.serializer.SnappyRedisSerializer;
import com.redis.sentinel.cache.serializer.StringRedisSerializer;

@Component
public class JedisClient {

	@Autowired
	private JedisSentinelPool jedisSentinelPool;
	private final StringRedisSerializer regionSerializer = new StringRedisSerializer();
	private final StringRedisSerializer keySerializer = new StringRedisSerializer();
	private final RedisSerializer<Object> valueSerializer = new SnappyRedisSerializer<Object>();

	public Jedis getResource() {
		return jedisSentinelPool.getResource();
	}

	public void returnResource(Jedis jedis) {
		jedisSentinelPool.returnResource(jedis);
	}

	public void returnBrokenResource(Jedis jedis) {
		jedisSentinelPool.returnBrokenResource(jedis);
	}

	/**
	 * save cache item
	 *
	 * @param region
	 *            region name
	 * @param key
	 *            cache key to save
	 * @param value
	 *            cache value to save
	 * @param timeout
	 *            expire timeout
	 * @param unit
	 *            expire timeout unit
	 */
	public void hset(final String region, final Object key, final Object value) {
		final byte[] rawRegion = rawRegion(region);
		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(value);

		runWithTx(new JedisTransactionalCallback() {
			@Override
			public void execute(Transaction tx) {
				tx.hset(rawRegion, rawKey, rawValue);
			}
		});
	}

	public void hsetJson(final String region, final String key,
			final Object value) {
		runWithTx(new JedisTransactionalCallback() {
			@Override
			public void execute(Transaction tx) {
				tx.hset(region, key, JSON.toJSONString(value));
			}
		});
	}

	/**
	 * Get cache
	 *
	 * @param region
	 *            region name
	 * @param key
	 *            cache key
	 * @param expirationInSeconds
	 *            expiration timeout in seconds
	 * @return return cached entity, if not exists return null.
	 */
	public Object hget(final String region, final Object key) {
		final byte[] rawRegion = rawRegion(region);
		final byte[] rawKey = rawKey(key);
		byte[] rawValue = callBack(new JedisCallback<byte[]>() {
			@Override
			public byte[] execute(Jedis jedis) {
				return jedis.hget(rawRegion, rawKey);
			}
		});

		return deserializeValue(rawValue);
	}

	public <T> T hgetJson(final String region, final String key, Class<T> clazz) {
		try {
			String rawValue = callBack(new JedisCallback<String>() {
				@Override
				public String execute(Jedis jedis) {
					return jedis.hget(region, key);
				}
			});

			return JSON.parseObject(rawValue, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * get all cached items in specified region
	 *
	 * @param region
	 *            region name
	 * @return map of keys and all cached items in specified region
	 */
	public Map<Object, Object> hgetAll(String region) {
		final byte[] rawRegion = rawRegion(region);
		Map<byte[], byte[]> rawMap = callBack(new JedisCallback<Map<byte[], byte[]>>() {
			@Override
			public Map<byte[], byte[]> execute(Jedis jedis) {
				return jedis.hgetAll(rawRegion);
			}
		});

		Map<Object, Object> map = new HashMap<Object, Object>();
		for (Map.Entry<byte[], byte[]> entry : rawMap.entrySet()) {
			Object key = deserializeKey(entry.getKey());
			Object value = deserializeValue(entry.getValue());
			map.put(key, value);
		}
		return map;
	}

	/**
	 * confirm the specified cache item in specfied region
	 *
	 * @param region
	 *            region name
	 * @param key
	 *            cache key
	 */
	public boolean exists(final String region, final Object key) {
		final byte[] rawRegion = rawRegion(region);
		final byte[] rawKey = rawKey(key);

		return callBack(new JedisCallback<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.hexists(rawRegion, rawKey);
			}
		});
	}

	/**
	 * delete cache item in specified region.
	 *
	 * @param region
	 *            region name
	 * @param key
	 *            cache key to delete
	 * @return count of deleted key
	 */
	public Long del(final String region, final Object key) {
		final byte[] rawRegion = rawRegion(region);
		final byte[] rawKey = rawKey(key);

		runWithTx(new JedisTransactionalCallback() {
			@Override
			public void execute(Transaction tx) {
				tx.hdel(rawRegion, rawKey);
			}
		});

		return 1L;
	}

	// public Map<String, String> hgetAll(String key) {
	// Jedis jedis = null;
	// try {
	// jedis = getResource();
	// return jedis.hgetAll(key);
	// } catch (Exception e) {
	// returnBrokenResource(jedis);
	// e.printStackTrace();
	// } finally {
	// returnResource(jedis);
	// }
	// return new HashMap<String, String>();
	// }
	/**
	 * execute the specified callback
	 */
	private <T> T callBack(final JedisCallback<T> callback) {

		Jedis jedis = jedisSentinelPool.getResource();
		try {
			return callback.execute(jedis);
		} finally {
			returnResource(jedis);
		}
	}

	/**
	 * execute the specified callback under transaction HINT:
	 *
	 * @param callback
	 *            executable instance under transaction
	 */
	private List<Object> runWithTx(final JedisTransactionalCallback callback) {

		Jedis jedis = jedisSentinelPool.getResource();
		try {
			Transaction tx = jedis.multi();
			callback.execute(tx);
			return tx.exec();
		} finally {
			returnResource(jedis);
		}
	}

	/**
	 * serialize cache key
	 */
	private byte[] rawKey(final Object key) {
		return keySerializer.serialize(key.toString());
	}

	/**
	 * serializer cache value
	 */
	private byte[] rawValue(final Object value) {
		try {
			return valueSerializer.serialize(value);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * deserialize key
	 */
	private Object deserializeKey(final byte[] rawKey) {
		return keySerializer.deserialize(rawKey);
	}

	/**
	 * deserialize raw value
	 */
	private Object deserializeValue(final byte[] rawValue) {
		return valueSerializer.deserialize(rawValue);
	}

	/**
	 * serializer region name
	 */
	private byte[] rawRegion(final String region) {
		return regionSerializer.serialize(region);
	}

}