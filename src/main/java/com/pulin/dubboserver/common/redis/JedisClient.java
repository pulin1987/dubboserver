package com.pulin.dubboserver.common.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class JedisClient {

    private static final Logger logger = LoggerFactory.getLogger(JedisClient.class);

    public JedisPool jedisPoolMaster;

    public JedisPool jedisPoolSlave;

    /**
     * 释放连接
     *
     * @param jedis
     * @param jedisPool
     * @param isBroken
     */
    public void release(Jedis jedis, JedisPool jedisPool, boolean isBroken) {
        if (jedis != null) {
            if (isBroken) {
                jedisPool.returnBrokenResource(jedis);
            } else {
                jedisPool.returnResource(jedis);
            }
        }
    }

    /**
     * 序列化
     *
     * @param object
     * @return
     * @author 
     * @2015年3月9日
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("serialize failed");
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                logger.warn("serialize failed");
            }
        }
        return null;
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @return
     * @author 
     * @2015年3月9日
     */
    public static Object deserialize(byte[] bytes) {
        try {
            return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        } catch (Exception e) {
            logger.warn("deserialize failed");
        }
        return null;
    }

    /**
     * 存储obj
     *
     * @param key
     * @param value
     * @param expireTime
     */
    public void setObj(String key, Object value, int expireTime) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = jedisPoolMaster.getResource();
            if (expireTime == 0) {// 永不过期
                jedis.set(key.getBytes(), serialize(value));
            } else
                jedis.setex(key.getBytes(), expireTime, serialize(value));
        } catch (Exception e) {
            logger.warn("failed : jedis set obj key : " + key);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolMaster, isBroken);
        }
    }

    /**
     * 获取obj
     *
     * @param key
     * @return
     */
    public Object getObj(String key) {
        Jedis jedis = null;
        boolean isBroken = false;
        Object value = null;
        try {
            jedis = jedisPoolSlave.getResource();
            value = deserialize(jedis.get(key.getBytes()));
        } catch (Exception e) {
            logger.warn("failed : jedis get obj key : " + key);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolSlave, isBroken);
        }
        return value;
    }

    /**
     * 删除
     *
     * @param key
     */
    public void delObj(String key) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = jedisPoolMaster.getResource();
            jedis.del(key.getBytes());
        } catch (Exception e) {
            logger.warn("failed : jedis delete obj key : " + key);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolMaster, isBroken);
        }
    }

    /**
     * 模糊删除
     *
     * @param regex
     * @2015年3月9日
     */
    public void fuzzyDel(String regex) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = jedisPoolMaster.getResource();
            Set<String> keys = jedis.keys(regex);
            for (String key : keys) {
                del(key);
            }
        } catch (Exception e) {
            logger.warn("failed : jedis fuzzyDel key : " + regex);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolMaster, isBroken);
        }
    }

    /**
     * 保存HashMap
     *
     * @param key
     * @param value
     * @param expireTime
     * @2015年3月9日
     */
    public void hmset(String key, Map<String, String> value, int expireTime) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = jedisPoolMaster.getResource();
            jedis.hmset(key, value);
            if (expireTime != 0) {
                jedis.expire(key, expireTime);
            }
        } catch (Exception e) {
            logger.warn("failed : jedis hmset key : " + key + " , value :" + value + " , expireTime :" + expireTime);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolMaster, isBroken);
        }
    }

    /**
     * 获取hash
     *
     * @param key
     * @param fields
     * @return
     */
    public List<String> hmget(String key, String... fields) {
        Jedis jedis = null;
        boolean isBroken = false;
        List<String> list = null;
        try {
            jedis = jedisPoolSlave.getResource();
            list = jedis.hmget(key, fields);
        } catch (Exception e) {
            logger.warn("failed : jedis hmget key : " + key);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolSlave, isBroken);
        }
        return list;
    }

    /**
     * 模糊查询
     *
     * @param regex
     * @return
     * @2015年3月9日
     */
    public List<String> fuzzyGet(String regex) {
        Jedis jedis = null;
        boolean isBroken = false;
        List<String> list = null;
        try {
            jedis = jedisPoolSlave.getResource();
            Set<String> keys = jedis.keys(regex);
            if (keys != null && !keys.isEmpty()) {
                list = new ArrayList<String>();
                for (String key : keys) {
                    list.add(get(key));
                }
            }
        } catch (Exception e) {
            logger.warn("failed : jedis fuzzyGet key : " + regex);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolSlave, isBroken);
        }
        return list;
    }

    // set to master
    public void set(String key, String value, int expireTime) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = jedisPoolMaster.getResource();
            jedis.set(key, value);
            if (expireTime != 0) {
                jedis.expire(key, expireTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("failed : jedis set key : " + key + " , value :" + value + " , expireTime :" + expireTime);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolMaster, isBroken);
        }
    }

    // get from slave
    public String get(String key) {
        Jedis jedis = null;
        boolean isBroken = false;
        String value = null;
        try {
            jedis = jedisPoolSlave.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            logger.warn("failed : jedis get key : " + key);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolSlave, isBroken);
        }
        return value;
    }

    /**
     * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1
     * GB).
     *
     * @param key
     * @param value
     * @param nxxx  NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
     *              if it already exist.
     * @param expx  EX|PX, expire time units: EX = seconds; PX = milliseconds
     * @param time  expire time in the units of <code>expx</code>
     * @return Status code reply
     */
    public String set(String key, String value, String nxxx, String expx, long time) {
        Jedis jedis = null;
        String reply = null;
        boolean isBroken = false;
        try {
            jedis = jedisPoolMaster.getResource();
            reply = jedis.set(key, value, nxxx, expx, time);
        } catch (Exception e) {
            logger.warn("failed : jedis get key : " + key);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolMaster, isBroken);
        }
        return reply;
    }

    /**
     * Redis can notify Pub/Sub clients about events happening in the key space.
     * This feature is documented at http://redis.io/topics/notifications
     * <p>
     * For instance if keyspace events notification is enabled, and a client
     * performs a DEL operation on key "foo" stored in the Database 0, two
     * messages will be published via Pub/Sub:
     * <p>
     * PUBLISH __keyspace@0__:foo del
     * PUBLISH __keyevent@0__:del foo
     * <p>
     * It is possible to select the events that Redis will notify among a set
     * of classes. Every class is identified by a single character:
     * <p>
     * K     Keyspace events, published with __keyspace@<db>__ prefix.
     * E     Keyevent events, published with __keyevent@<db>__ prefix.
     * g     Generic commands (non-type specific) like DEL, EXPIRE, RENAME, ...
     * $     String commands
     * l     List commands
     * s     Set commands
     * h     Hash commands
     * z     Sorted set commands
     * x     Expired events (events generated every time a key expires)
     * e     Evicted events (events generated when a key is evicted for maxmemory)
     * A     Alias for g$lshzxe, so that the "AKE" string means all the events.
     * <p>
     * The "notify-keyspace-events" takes as argument a string that is composed
     * of zero or multiple characters. The empty string means that notifications
     * are disabled.
     * <p>
     * Example: to enable list and generic events, from the point of view of the
     * event name, use:
     * <p>
     * notify-keyspace-events Elg
     * <p>
     * Example 2: to get the stream of the expired keys subscribing to channel
     * name __keyevent@0__:expired use:
     * <p>
     * notify-keyspace-events Ex
     * <p>
     * By default all notifications are disabled because most users don't need
     * this feature and the feature has some overhead. Note that if you don't
     * specify at least one of K or E, no events will be delivered.
     *
     * @param jedisPubSub
     * @param patterns
     */
    public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
        jedisPoolMaster.getResource().psubscribe(jedisPubSub, patterns);
    }

    // del from mater
    public void del(String key) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = jedisPoolMaster.getResource();
            jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("failed : jedis delete key : " + key);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolMaster, isBroken);
        }
    }

    public Long incr(String key) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = jedisPoolMaster.getResource();
            return jedis.incr(key);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("failed : jedis incr key : " + key);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolMaster, isBroken);
        }
        return -1L;
    }

    //设置在millisecondsTimestamp毫秒时间点失效
    public Long incrForTody(String key, long millisecondsTimestamp) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = jedisPoolMaster.getResource();
            long incrRturn = jedis.incr(key);
            	if (incrRturn==1L) {
                	jedis.pexpireAt(key, millisecondsTimestamp);
    			}
            logger.info("incrRturn ::"+incrRturn);
            return incrRturn;
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("failed : jedis incrForTody key : " + key);
            isBroken = true;
        } finally {
            release(jedis, jedisPoolMaster, isBroken);
        }
        return -1L;
    }
    
    public Long pexpireAt(String key,long millisecondsTimestamp) {
    	Jedis jedis = null;
    	boolean isBroken = false;
    	try {
    		jedis = jedisPoolMaster.getResource();
    		return jedis.pexpireAt(key, millisecondsTimestamp);
    	} catch (Exception e) {
    		e.printStackTrace();
    		logger.warn("failed : jedis pexpireAt key : " + key);
    		isBroken = true;
    	} finally {
    		release(jedis, jedisPoolMaster, isBroken);
    	}
    	return -1L;
    }

    
    public JedisPool getJedisPoolMaster() {
        return jedisPoolMaster;
    }

    public void setJedisPoolMaster(JedisPool jedisPoolMaster) {
        this.jedisPoolMaster = jedisPoolMaster;
    }

    public JedisPool getJedisPoolSlave() {
        return jedisPoolSlave;
    }

    public void setJedisPoolSlave(JedisPool jedisPoolSlave) {
        this.jedisPoolSlave = jedisPoolSlave;
    }
}