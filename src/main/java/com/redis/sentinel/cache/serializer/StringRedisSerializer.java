package com.redis.sentinel.cache.serializer;

/**
 * UTF-8 String serializer
 *
 * @author yaochong.chen
 */
public class StringRedisSerializer implements RedisSerializer<String> {

	@Override
	public byte[] serialize(String str) {
		return ((str == null) || (str.length() == 0)) ? EMPTY_BYTES : str
				.getBytes(UTF_8);
	}

	@Override
	public String deserialize(byte[] bytes) {
		return ((bytes == null) || (bytes.length == 0)) ? "" : new String(
				bytes, UTF_8);
	}
}
