/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.redis.sentinel.cache.serializer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

/**
 * SnappyRedisSerializer Created by debop on 2014. 3. 15.
 */
public class SnappyRedisSerializer<T> implements RedisSerializer<T> {
    private static final Logger log = LoggerFactory.getLogger(SnappyRedisSerializer.class);
    private final RedisSerializer<T> inner;

    public SnappyRedisSerializer() {
        this(new FstRedisSerializer<T>());
    }

    public SnappyRedisSerializer(RedisSerializer<T> innerSerializer) {
        assert (innerSerializer != null);
        this.inner = innerSerializer;
    }

    @Override
    public byte[] serialize(T graph) {
        try {
            return Snappy.compress(inner.serialize(graph));
        } catch (IOException e) {
            log.error("Fail to serialize graph.", e);
            return EMPTY_BYTES;
        }
    }

    @Override
    public T deserialize(byte[] bytes) {
        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }
        try {
            T t = inner.deserialize(Snappy.uncompress(bytes));
            return t;
        } catch (IOException e) {
            log.error("Fail to deserialize graph.", e);
            e.printStackTrace();
            return null;
        }
    }
}
