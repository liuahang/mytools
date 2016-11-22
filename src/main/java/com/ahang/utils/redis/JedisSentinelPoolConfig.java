package com.ahang.utils.redis;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

public class JedisSentinelPoolConfig {
	    private String masterName;
	    private String sentinels;
	    private JedisPoolConfig jedisConfig;

	    public JedisSentinelPoolConfig(String masterName, String sentinels, JedisPoolConfig jedisConfig) {
	        this.masterName = masterName;
	        this.sentinels = sentinels;
	        this.jedisConfig = jedisConfig;
	    }

	    public JedisSentinelPool getJedisSentinelPool() {
	        String[] sentinelstrs = sentinels.split(",");
	        Set<String> sentinelsSet = new HashSet<String>(sentinelstrs.length);
	        for (String st : sentinelstrs) {
	            sentinelsSet.add(st);
	        }
	        return new JedisSentinelPool(masterName, sentinelsSet, jedisConfig);
	    }
}
