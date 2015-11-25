package com.redis;

public interface RedisService {
	
	public final static long ONE_MONTH = 259200L;
	
	/**
	 * 通过key删除
	 * @param key
	 */
	public long del(String... keys);

	/**
	 * 添加key value 并且设置存活时间
	 * @param key
	 * @param value
	 * @param liveTime 单位秒
	 */
	public void set(String key, String value, long liveTime);

	/**
	 * 添加key value
	 * @param key
	 * @param value
	 */
	public void set(String key, String value);

	/**
	 * 获取redis value (String)
	 * @param key
	 * @return
	 */
	public String get(String key);

	/**
	 * 检查key是否已经存在
	 * @param key
	 * @return
	 */
	public boolean exists(String key);

	/**
	 * 设置key的生命周期
	 * @param key
	 * @param seconds 单位(秒)
	 * @return
	 */
	public boolean expire(final String key, final long seconds);
	
    public long eval(final String luaCommand);
	
}
