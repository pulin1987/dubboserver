package com.pulin.dubboserver.service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class ZookeeperPropertiesService {
	
	private AtomicBoolean initState = new AtomicBoolean(false);  
	
	 Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

	    @Autowired
	    private CuratorFramework client;

	    @Value("${spring.application.name}")
	    private String appName;
	    
	    @Value("${spring.cloud.zookeeper.config.root}")
	    private String zkRoot;

	    @Autowired
	    private Environment env;
	    
	    private RelaxedPropertyResolver relaxedPropertyResolver;

	    /**
	     * 监控zk配置文件更新并自动更新到本地内存
	     * zk会覆盖本地的property
	     * @throws Exception
	     */
	    //@PostConstruct
	    
//	    private void installZkConfig() throws Exception {
//	    	System.out.println(client.getZookeeperClient().getCurrentConnectionString());
//	    	log.info("zkRoot:{}",zkRoot);
//	    	log.info("appName:{}",appName);
//	    	System.out.println("zkRoot"+":"+zkRoot);
//	    	System.out.println(appName+":"+appName);
//	        String zkConfigRootPath = zkRoot + "/" + appName;
//	        ZooKeeperConfigurationSource zkConfigSource = new ZooKeeperConfigurationSource(client, zkConfigRootPath);
//	        zkConfigSource.start();
//	        DynamicWatchedConfiguration zkDynamicConfig = new DynamicWatchedConfiguration(zkConfigSource);
//	        ConfigurationManager.install(zkDynamicConfig);
//	    }

	    /*
	     * 获取zookeeper配置的属性值
	     * zk取不到会在本地找
	     * 使用该方法应该使用DynamicWatchedConfiguration和ZooKeeperConfigurationSource以支持zk缓存到本地
	     * @param key
	     * @return
	     */
	    
	 /*   public String getPropertyValue(String key) {
	        String value = ConfigurationManager.getConfigInstance().getString(key);
	        if (StringUtils.isEmpty(value)) {
	            throw new NullPointerException("property key : [" + key + "] value is not found");
	        }
	        return value;
	    }*/
	    
	    private RelaxedPropertyResolver init(){
	    	//如果当前AtomicBoolean对象的值与expect相等，那么我们就去更新值为update，并且返回true，否则返回false
	    	//当前值与expect相比较。如果相等继续第二步，如果不相等直接返回false
	    	//把当前值更新为update，并返回为true
	    	 if (initState.compareAndSet(false, true)) {//init once
	    		 relaxedPropertyResolver = new RelaxedPropertyResolver(env);
	             return relaxedPropertyResolver;
	         }  
	    	 return relaxedPropertyResolver;
	    }
	   
	    /**
	     * 获取zookeeper配置的属性值
	     * @param key
	     * @return
	     */
	    public String getPropertyValue(String key) {
	    	if(relaxedPropertyResolver == null){
	    		relaxedPropertyResolver = init();
	    	}
	        String property = relaxedPropertyResolver.getProperty(key, "Not Found");
	        if (StringUtils.isBlank(property)) {
	            throw new NullPointerException("property key : " + key + " value is null or blank");
	        }
	        return property;
	    }

	    /**
	     * 获取zookeeper配置节点下的所有值
	     * @param node
	     * @return
	     */
	    public Map<String, Object> getPropertyValues(String node) {
	        Map<String, Object> map = new RelaxedPropertyResolver(env).getSubProperties("member");
	        if (map == null || map.isEmpty()) {
	            throw new NullPointerException("property node : " + node + " values is null");
	        }
	        return map;
	    }

}
