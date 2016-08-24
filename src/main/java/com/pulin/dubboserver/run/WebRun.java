package com.pulin.dubboserver.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import lombok.extern.slf4j.Slf4j;

//@EnableAutoConfiguration
//@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, MongoDataAutoConfiguration.class,PropertySourceBootstrapConfiguration.class })
@EnableAutoConfiguration(exclude = {
		 DataSourceAutoConfiguration.class
		,MongoDataAutoConfiguration.class
        ,SolrAutoConfiguration.class
        ,ElasticsearchAutoConfiguration.class
       //,RedisAutoConfiguration.class
        })
@ComponentScan("com.pulin") // 组件扫描
//@EnableDiscoveryClient
//@EnableEurekaClient
@ImportResource(value = {"classpath:application-boot-start.xml"}) // 导入配置文件
@Slf4j
public class WebRun {


	public static void main(String[] args) throws Exception {
	    //System.getProperties().setProperty("server.port", "9120");
		log.info("server start...");
		SpringApplication.run(WebRun.class, args);
		log.info("server start end...");
	}
}
