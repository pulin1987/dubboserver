package com.pulin.dubboserver.redis;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {
	
	//config set requirepass redis 设置登录密码为redis   auth redis
	public static void setValue(){
		JedisPoolConfig config = new JedisPoolConfig();
		JedisPool pool = new JedisPool(config,"192.168.71.128",6379,60000,"redis");
		pool.getResource().setex("aaa", 200, "111");
	}
	
	public static void getValue(){
		JedisPoolConfig config = new JedisPoolConfig();
		JedisPool pool = new JedisPool(config,"192.168.71.128",6379,60000,"redis");
		String value = pool.getResource().get("aaa");
		System.out.println(value);
	}
	
	public static void main(String[] args) {
		getValue();
	}

}
