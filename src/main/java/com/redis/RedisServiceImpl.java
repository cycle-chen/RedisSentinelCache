package com.redis;
//package com.spring.redis;
//
//import java.io.UnsupportedEncodingException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.core.RedisCallback;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class RedisServiceImpl implements RedisService {
//
//	private static String redisCode = "utf-8";
//
//	@Autowired
//	private RedisTemplate<String, String> redisTemplate;
//
//	public long del(final String... keys) {
//		return redisTemplate.execute(new RedisCallback<Long>() {
//			public Long doInRedis(RedisConnection connection)
//					throws DataAccessException {
//				long result = 0;
//				for (String k : keys) {
//					result = connection.del(k.getBytes());
//				}
//				return result;
//			}
//		});
//	}
//
//	public void set(final byte[] key, final byte[] value, final long liveTime) {
//		redisTemplate.execute(new RedisCallback<Long>() {
//			public Long doInRedis(RedisConnection connection)
//					throws DataAccessException {
//				connection.set(key, value);
//				if (liveTime > 0) {
//					connection.expire(key, liveTime);
//				}
//				return 1L;
//			}
//		});
//
//	}
//
//	public void set(String key, String value, long liveTime) {
//		this.set(key.getBytes(), value.getBytes(), liveTime);
//
//	}
//
//	/**
//	 * 默认一个月
//	 */
//	public void set(String key, String value) {
//		this.set(key, value, ONE_MONTH);
//	}
//
//	public String get(final String key) {
//		if ((null == key) || "".equals(key)) {
//			return null;
//		}
//
//		return redisTemplate.execute(new RedisCallback<String>() {
//			public String doInRedis(RedisConnection connection)
//					throws DataAccessException {
//				byte[] result = connection.get(key.getBytes());
//				if (result != null) {
//					try {
//						return new String(result, redisCode);
//					} catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					}
//				}
//				return "";
//			}
//		});
//	}
//
//	public boolean exists(final String key) {
//		if ((null == key) || "".equals(key)) {
//			return false;
//		}
//
//		return redisTemplate.execute(new RedisCallback<Boolean>() {
//			public Boolean doInRedis(RedisConnection connection)
//					throws DataAccessException {
//				return connection.exists(key.getBytes());
//			}
//
//		});
//	}
//
//	public boolean expire(final String key, final long seconds) {
//		if ((null == key) || "".equals(key)) {
//			return false;
//		}
//
//		return redisTemplate.execute(new RedisCallback<Boolean>() {
//			public Boolean doInRedis(RedisConnection connection)
//					throws DataAccessException {
//				return connection.expire(key.getBytes(), seconds);
//			}
//		});
//	}
//
//	public long eval(final String luaCommand) {
//		return redisTemplate.execute(new RedisCallback<Long>() {
//			public Long doInRedis(RedisConnection connection)
//					throws DataAccessException {
//				return connection.eval(luaCommand.getBytes(), null, 0);
//			}
//		});
//	}
//}
