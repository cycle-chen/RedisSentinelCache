package com.redis.sentinel.cache.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Serialization Helper class
 *
 * @author yaochong.chen
 */
public abstract class SerializationTool {

	static boolean isEmpty(byte[] data) {
		return ((data == null) || (data.length == 0));
	}

	@SuppressWarnings("unchecked")
	private static <T extends Collection<?>> T deserializeValues(
			Collection<byte[]> rawValues, Class<T> clazz,
			RedisSerializer<?> redisSerializer) {
		if (rawValues == null) {
			return null;
		}

		int valueCount = rawValues.size();
		Collection<Object> values = List.class.isAssignableFrom(clazz) ? new ArrayList<Object>(
				valueCount) : new HashSet<Object>(valueCount);

		for (byte[] bs : rawValues) {
			values.add(redisSerializer.deserialize(bs));
		}
		return (T) values;
	}

	@SuppressWarnings("unchecked")
	public static <T> Set<T> deserialize(Set<byte[]> rawValues,
			RedisSerializer<T> redisSerializer) {
		return deserializeValues(rawValues, Set.class, redisSerializer);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> deserialize(List<byte[]> rawValues,
			RedisSerializer<T> redisSerializer) {
		return deserializeValues(rawValues, List.class, redisSerializer);
	}

	@SuppressWarnings("unchecked")
	public static <T> Collection<T> deserialize(Collection<byte[]> rawValues,
			RedisSerializer<T> redisSerializer) {
		return deserializeValues(rawValues, List.class, redisSerializer);
	}
}
