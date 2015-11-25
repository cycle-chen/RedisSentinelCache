package com.redis.sentinel.config;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public abstract class RedisSentinel {

	public JedisPool jedisPool = null;
	protected RedisPoolConfig poolConfig = new RedisPoolConfig();

	protected Jedis jedisSentinel = null;

	private String nextMaster = null;
	private String upMaster = null;

	/**
	 * 创建多个集群共享的连接池
	 */
	protected void createJedisPool(String... clusterName) {
		List<String> redisInfo = jedisSentinel
				.sentinelGetMasterAddrByName(clusterName[0]);
		String host = redisInfo.get(0);
		int port = Integer.parseInt(redisInfo.get(1));

		jedisPool = new JedisPool(poolConfig, host, port);
	}

	/**
	 * 检查redis sentinel 主/从redis服务是否正常, 当发生故障时，当redis sentinel监控自动切换 从redis
	 * 升级为主redis，需要重新初始化jedispool
	 *
	 * @param redisSentinel
	 *            哪种监控的连接池（是否分片)
	 * @param againCheckTime
	 *            单位（毫秒/millisecond）
	 * @param clusterName
	 *            集群名
	 */
	protected void checkRedisSentinelServer(final RedisSentinel redisSentinel,
			final int againCheckTime, final String... clusterName) {
		System.out.println("检查redis sentinel 主/从redis服务是否正常,任务开始...");
		Runnable checkRedisSentinelRunnable = new Runnable() {
			public void run() {
				try {
					while (true) {
						jedisSentinel.ping();
						List<String> masters = jedisSentinel
								.sentinelGetMasterAddrByName(clusterName[0]);
						String master = masters.toString();
						if ((upMaster == null) || upMaster.equals("")) {
							upMaster = master;
						}
						if ((nextMaster == null) || nextMaster.equals("")
								|| !nextMaster.equals(master)) {
							nextMaster = master;
						}
						if (nextMaster.equals(upMaster)) {
							continue;
						}
						System.out.println("主redis发生故障，自动切换...");
						if (redisSentinel instanceof RedisSentinelJedisPool) {
							createJedisPool(clusterName); // 重新初始化jedispool
							System.out.println("重新初始化jedispool...");
						}
						upMaster = nextMaster;
						Thread.sleep(againCheckTime);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("redis sentinel 监控异常,请检查...");
				}
			}
		};
		new Thread(checkRedisSentinelRunnable).start();
	}

	/**
	 * 获取客户端连接池
	 *
	 * @return
	 */
	public abstract Object getResource();

	/**
	 * 返回一个客户端连接到连接池
	 *
	 * @param resource
	 */
	public abstract void returnBrokenResource(Jedis resource);

	public RedisPoolConfig getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(RedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}

}