package com.redis.sentinel.cache.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serializer using Fast-Serialization
 *
 * @author Sunghyouk Bae
 */
public class FstRedisSerializer<T> implements RedisSerializer<T> {
	private static final Logger log = LoggerFactory
			.getLogger(FstRedisSerializer.class);
	private static final FSTConfiguration conf = FSTConfiguration
			.createDefaultConfiguration();

	/**
	 * Provides access to serialization configuration, to inject custom
	 * ClassLoaders among other things.
	 *
	 * @return serialization configuration.
	 */
	public static FSTConfiguration getConf() {
		return conf;
	}

	@Override
	public byte[] serialize(final T graph) {
		if (graph == null) {
			return EMPTY_BYTES;
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			FSTObjectOutput oos = conf.getObjectOutput(os);
			try {
				oos.writeObject(graph);
				oos.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != oos) {
					oos.close();
				}
			}
			return os.toByteArray();
		} catch (Exception e) {
			log.warn("Fail to serializer graph. graph=" + graph, e);
			return EMPTY_BYTES;
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public T deserialize(final byte[] bytes) {
		if (SerializationTool.isEmpty(bytes)) {
			return null;
		}

		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		try {
			FSTObjectInput ois = conf.getObjectInput(is);
			try {
				return (T) ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			log.warn("Fail to deserialize bytes.", e);
			return null;
		}
	}
}
