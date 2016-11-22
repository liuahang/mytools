package com.ahang.utils.redis;

import redis.clients.jedis.JedisPoolConfig;

/**
 * #redis config
redis.master.name=mir-mon-redis-master
redis.sentinels=172.16.80.97:26379
spring.redis.password=
spring.redis.pool.max-total=300
spring.redis.pool.min-idle=0
spring.redis.pool.max-active=8
spring.redis.pool.max-wait=30000
spring.redis.timeout=300

redis的初始化值可以从配置文件中读取，RedisService可以通过spring的方式
初始化，加@Service注解
		
 * @author liuhang
 *
 */
public class RedisService {
	
	
	public JedisHelper getRedisHelper(){
		  JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxWaitMillis(1000);
        JedisSentinelPoolConfig JedisSentinelPoolConfig  = new JedisSentinelPoolConfig("master-name","",jedisPoolConfig);
        JedisHelper helper = new JedisHelper(JedisSentinelPoolConfig);
		  helper.setJedisSentinelPoolConfig(JedisSentinelPoolConfig);
		return helper;
	}  
}
