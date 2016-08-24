package com.pulin.dubboserver;

import java.net.URI;
import java.net.URISyntaxException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestRedisConnection {
	
	public static void main(String[] args) {
		
		String url="redis://d64ffed6b62e4e7c:CcPEtGSkIXSESQ6R@d64ffed6b62e4e7c.m.cnhza.kvstore.aliyuncs.com:6379/3";
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(60000);//设置最大连接数  
	    config.setMaxIdle(1000); //设置最大空闲数 
	    config.setMaxWaitMillis(3000);//设置超时时间  
	    config.setTestOnBorrow(true);
	    
	    URI uri = null;
	    try {
			 uri = new URI(url);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
	    JedisPool pool = new JedisPool(config,uri,1000);
	    
	    //JedisPool pool = new JedisPool(config,url,6379,1000);
		//JedisPool pool = new JedisPool(config,"192.168.71.128",6379,1000);
		
	    System.out.println(pool);
	    System.out.println(pool.getNumActive());
	    try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    
	    
		Jedis jedis = pool.getResource();
		//Jedis jedis = new Jedis(url);
		jedis.setex("test", 120, "test");
		String value = jedis.get("test");
		System.out.println(value);
		pool.returnResource(jedis);
	}

}
