package com.redis.sentinel.cache.serializer.protostuff;

public interface Deserializer {
	<T> T deserialize(final byte[] bytes, final Class<T> clazz);
}
