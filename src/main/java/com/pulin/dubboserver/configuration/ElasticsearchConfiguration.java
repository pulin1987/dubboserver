package com.pulin.dubboserver.configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = "classpath:elasticsearch.properties")
//@EnableElasticsearchRepositories(basePackages = "com.pulin")
public class ElasticsearchConfiguration {
	
	@Autowired(required=false)
	private Environment environment;
	
	

	@Bean
	public Client client() {
		
   /*	TransportClient client = new TransportClient();
		TransportAddress address = new InetSocketTransportAddress(
				environment.getProperty("elasticsearch.host"),
				Integer.parseInt(environment.getProperty("elasticsearch.port"))
				);
		client.addTransportAddress(address);
		
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "xxx").build();
        client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(ipAddress, 9300));
		*/
		
		
		Client client = null;
		try {
			client = TransportClient.builder().build()
					 .addTransportAddress(
							 new InetSocketTransportAddress(
									 InetAddress.getByName(environment.getProperty("elasticsearch.host")), 
									 Integer.parseInt(environment.getProperty("elasticsearch.port"))
													 ));
			//client.settings().settingsBuilder().put("cluster.name", "xxx");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
		
		return client;
	}

	/*@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		return new ElasticsearchTemplate(client());
	}*/
	
	/*@Bean
	public ElasticsearchTemplate elasticsearchTemplate() {
		return new ElasticsearchTemplate(client());
	}*/
	
	/*public static void main(String[] args) {
		try {
			Client client = TransportClient.builder().build()
					 .addTransportAddress(
							 new InetSocketTransportAddress(
									 InetAddress.getByName("192.168.71.128"), 
									 9300));
			
			System.out.println(client);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}*/
}