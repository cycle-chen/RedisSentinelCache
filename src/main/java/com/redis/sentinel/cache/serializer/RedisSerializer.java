package com.redis.sentinel.cache.serializer;

import java.nio.charset.Charset;

/**
 * Serializer for Redis Key or Value
 *
 * @author yaochong.chen
 */
public interface RedisSerializer<T> {

	public byte[] EMPTY_BYTES = new byte[0];

	public Charset UTF_8 = Charset.forName("UTF-8");

	/**
	 * Serialize Object
	 */
	byte[] serialize(final T graph);

	/**
	 * Deserialize to object
	 */
	T deserialize(final byte[] bytes);
}
