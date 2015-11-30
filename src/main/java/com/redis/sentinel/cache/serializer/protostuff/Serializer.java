package com.redis.sentinel.cache.serializer.protostuff;

public interface Serializer {
	<T> byte[] serialize(T source);
}
