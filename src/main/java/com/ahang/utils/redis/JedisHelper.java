package com.ahang.utils.redis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Transaction;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JedisHelper {

	 private  JedisSentinelPool jedisPool;
	    private  ObjectMapper objectMapper = new ObjectMapper();
	    private  ThreadLocal<Transaction> threadTrans = new ThreadLocal<>();
	    private  ThreadLocal<Jedis> threadJedis = new ThreadLocal<>();

	    public JedisHelper(JedisSentinelPoolConfig jedisSentinelPoolConfig) {
	        super();
	    }
	    
	    public  void setJedisSentinelPoolConfig(JedisSentinelPoolConfig jedisSentinelPoolConfig){
	        jedisPool = jedisSentinelPoolConfig.getJedisSentinelPool();

	    }

	    public void destroy() {
	        if (null != jedisPool) {
	            jedisPool.destroy();
	        }
	    }

	    public void begin() {
	        Jedis jedis = threadJedis.get();
	        if (null == jedis) {
	            jedis = jedisPool.getResource();
	            threadJedis.set(jedis);
	        }
	        Transaction trans = threadTrans.get();
	        if (null == trans) {
	            trans = jedis.multi();
	            threadTrans.set(trans);
	        }
	    }

	    public  void  commit() {
	        Assert.notNull(threadTrans.get());
	        Assert.notNull(threadJedis.get());
	        threadTrans.get().exec();
	        threadJedis.get().close();
	        threadTrans.remove();
	        threadJedis.remove();
	    }

	    public  void rollback() {
	        threadTrans.remove();
	        threadJedis.remove();
	    }
	    
	    public <V> void set(String key, V value) throws IOException {
	        Assert.notNull(key);
	        Assert.notNull(value);
	        Jedis jedis = null;
	        try {
	            Transaction trans = threadTrans.get();
	            String serialized = objectMapper.writeValueAsString(value);
	            if (null == trans) { // not in transaction
	                jedis = jedisPool.getResource();
	                jedis.set(key, serialized);
	            } else {
	                trans.set(key, serialized);
	            }
	        } finally {
	            if (jedis != null) {
	                jedis.close();
	            }
	        }
	    }

	    public String get(String key) throws IOException {
	        Assert.notNull(key);
	        Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            String value =  jedis.get(key);
	            if (null == value) {
	                return null;
	            }
	            return objectMapper.readValue(value, String.class);
	        } finally {
	            if (jedis != null) {
	                jedis.close();
	            }
	        }
	    }

	    public  <V> V get(String key, Class<V> clazz) throws IOException {
	        Assert.notNull(key);
	        Assert.notNull(clazz);
	        Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            String value = jedis.get(key);
	            if (null == value) {
	                return null;
	            }
	            return objectMapper.readValue(value, clazz);
	        } finally {
	            if (jedis != null) { // not in transaction
	                jedis.close();
	            }
	        }
	    }

	    public  <V> List<V> getList(String key, Class<V> clazz) throws IOException {
	        Assert.notNull(key);
	        Assert.notNull(clazz);
	        Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            String value = jedis.get(key);
	            if (null == value) {
	                return null;
	            }
	            JavaType type = TypeFactory.defaultInstance().constructCollectionType(List.class, clazz);
	            return objectMapper.readValue(value, type);
	        } finally {
	            if (jedis != null) { // not in transaction
	                jedis.close();
	            }
	        }
	    }

	    public  void delete(String key) {
	        Assert.notNull(key);
	        Jedis jedis = null;
	        try {
	            Transaction trans = threadTrans.get();
	            if (null == trans) {
	                jedis = jedisPool.getResource();
	                jedis.del(key);
	            } else { // in transaction
	                trans.del(key);
	            }
	        } finally {
	            if (jedis != null) {
	                jedis.close();
	            }
	        }
	    }

	    public  void listAppend(String listName, Object... objects) throws IOException {
	        Assert.notNull(listName);
	        Assert.notNull(objects);
	        Jedis jedis = null;
	        try {
	            Transaction trans = threadTrans.get();
	            String[] elements = getSerializedStrings(objects);
	            if (null == trans) {
	                jedis = jedisPool.getResource();
	                jedis.rpush(listName, elements);
	            } else {
	                trans.rpush(listName, elements);
	            }
	        } finally {
	            if (jedis != null) { // not in transaction
	                jedis.close();
	            }
	        }
	    }

	    private  String[] getSerializedStrings(Object... objects) throws IOException {
	        String[] elements = new String[objects.length];
	        for (int i = 0; i < objects.length; ++i) {
	            elements[i] = getSerializedString(objects[i]);
	        }
	        return elements;
	    }

	    private  String getSerializedString(Object obj) throws IOException {
	        return objectMapper.writeValueAsString(obj);
	    }

	    public  void listPush(String listName, Object... objects) throws IOException {
	        Assert.notNull(listName);
	        Assert.notNull(objects);
	        Jedis jedis = null;
	        try {
	            Transaction trans = threadTrans.get();
	            String[] elements = getSerializedStrings(objects);
	            if (null == trans) {
	                jedis = jedisPool.getResource();
	                jedis.lpush(listName, elements);
	            } else {
	                trans.lpush(listName, elements);
	            }
	        } finally {
	            if (jedis != null) { // not in transaction
	                jedis.close();
	            }
	        }
	    }

	    public  long listSize(String listName) {
	        Assert.notNull(listName);
	        Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.llen(listName);
	        } finally {
	            if (jedis != null) {
	                jedis.close();
	            }
	        }
	    }

	    public  List<String> listRange(String listName, long start, long end) throws IOException {
	        return listRange(listName, String.class, start, end);
	    }

	    public  <T> List<T> listRange(String listName, Class<T> elementClazz, long start, long end) throws IOException {
	        Assert.notNull(listName);
	        Assert.notNull(elementClazz);
	        Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            List<String> elements = jedis.lrange(listName, start, end);
	            List<T> objects = new ArrayList<>(elements.size());
	            for (String element : elements) {
	                objects.add(objectMapper.readValue(element, elementClazz));
	            }
	            return objects;
	        } finally {
	            if (jedis != null) {
	                jedis.close();
	            }
	        }
	    }

	    public  void listRemove(String listName, Object object) throws IOException {
	        Assert.notNull(listName);
	        Assert.notNull(object);
	        Jedis jedis = null;
	        try {
	            Transaction trans = threadTrans.get();
	            String serialized = getSerializedString(object);
	            if (null == trans) {
	                jedis = jedisPool.getResource();
	                jedis.lrem(listName, 0, serialized);
	            } else {
	                trans.lrem(listName, 0, serialized);
	            }
	        } finally {
	            if (jedis != null) {
	                jedis.close();
	            }
	        }
	    }

	    public long getCurrentTimeStamp() {
	        Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            List<String> time = jedis.time();
	            return Long.parseLong(time.get(0));
	        } finally {
	            if (jedis != null) {
	                jedis.close();
	            }
	        }
	    }

	    public Set<String> keys(String pattern) {
	        Assert.notNull(pattern);
	        Jedis jedis = null;
	        try {
	            Transaction trans = threadTrans.get();
	            if (null == trans) {
	                jedis = jedisPool.getResource();
	                return jedis.keys(pattern);
	            } else {
	                return trans.keys(pattern).get();
	            }
	        } finally {
	            if (jedis != null) {
	                jedis.close();
	            }
	        }
	    }

	    public void ping() {
	        Jedis jedis = null;
	        try {
	            jedis =  jedisPool.getResource();
	            jedis.ping();
	        } finally {
	            if (jedis != null) {
	                jedis.close();
	            }
	        }
	    }

	    public String info() {
	        Jedis jedis = null;
	        try {
	            jedis =  jedisPool.getResource();
	            return jedis.info();
	        } finally {
	            if (jedis != null) {
	                jedis.close();
	            }
	        }
	    }
}
