package com.pulin.dubboserver.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ClientFactory {
	
    public static CuratorFramework newClient(){
    	//ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
    	CuratorFramework client = CuratorFrameworkFactory.builder()
    			.connectString("192.168.71.128:2181,192.168.71.128:2182,192.168.71.128:2183")
    	        .sessionTimeoutMs(30000)  
    	        .connectionTimeoutMs(30000)  
    	        .canBeReadOnly(false)  
    	        .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
    	        //.retryPolicy(retryPolicy)
    	        .namespace("configs")
    	        .defaultData(null)
    	        .build();
    	
    	//client.start(); 
    	
    	return client;
    }
}
