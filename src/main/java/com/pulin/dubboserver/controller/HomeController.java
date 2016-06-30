package com.pulin.dubboserver.controller;

import java.util.Iterator;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.servlet.http.HttpServletRequest;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pulin.dubboserver.service.ZookeeperPropertiesService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HomeController implements EnvironmentAware{
	
	
	@Autowired(required=false)
	private JmsTemplate jmsTemplate;
	
	@Autowired(required=false)
	@Qualifier("notifyQueue")
	private Queue notifyQueue;
	
	@Autowired(required=false)
	private ZookeeperPropertiesService zookeeperPropertiesService;
	
	@RequestMapping("/api/zk")
	@ResponseBody
	public Object zookeeper(HttpServletRequest request) {
		String node = request.getParameter("node");
		 Map<String, Object> map = zookeeperPropertiesService.getPropertyValues(node);
		 Iterator<?> ite = map.keySet().iterator();
		 while(ite.hasNext()){
			 String key = ite.next().toString();
			 System.out.print(key+":");
			 System.out.println(map.get(key).toString());
		 }
		 return "ok";
	}
	
	@RequestMapping("/api/get")
	@ResponseBody
	public Object get(HttpServletRequest request) {
		String key = request.getParameter("key");
		String numStr = request.getParameter("num");
		int num = 1;
		if(StringUtils.isNoneBlank(numStr)){
		   num = Integer.parseInt(numStr);
		}
		
		long s = System.currentTimeMillis();
		String value = "error";
		for(int i=0;i<num;i++){
			 value = zookeeperPropertiesService.getPropertyValue(key);
		}
		System.out.println(value);
		long e = System.currentTimeMillis();
		System.out.println((e-s)/1000);
		return value;
	}
	
	@RequestMapping("/api/get/s1")
	@ResponseBody
	public Object gets1(HttpServletRequest request) {
		try{
			int num = 3/0;
		}catch(Exception e){
			log.error("e:{}",e);
		}
		return System.currentTimeMillis()+"";
	}
	
	@RequestMapping("/api/home")
	@ResponseBody
	public Object home() {
		
		String s = System.currentTimeMillis() + "";
		
		jmsTemplate.convertAndSend(notifyQueue,s);
	
//		try {
//			ActiveMQTextMessage message = new ActiveMQTextMessage();
//			//ScheduledMessage me = new ScheduledMessage();
//			message.setText(s);
//			message.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "* */5 * * *");
//			jmsTemplate.convertAndSend(notifyQueue,message);
//		} catch (JMSException e) {
//			e.printStackTrace();
//		}
		
		System.out.println(jmsTemplate);
		//延迟30秒，投递10次，间隔10秒:
		try {
			ActiveMQTextMessage message = new ActiveMQTextMessage();
			long delay = 30 * 1000 * 2 * 10;
			long period = 10 * 1000;
			int repeat = 9;
			message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
			//message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, period);
			//message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, repeat);
			jmsTemplate.convertAndSend(notifyQueue, message);
		} catch (JMSException e) {
			log.error("e:{}",e);
		}  catch (Exception ex) {
			log.error("ex:{}",ex);
		}
	      
		
		

		return s;
	}
	
	
	

	@Override
	public void setEnvironment(Environment environment) {
		 String s = environment.getProperty("JAVA_HOME");
	     System.out.println("JAVA_HOME:"+s);
		
	}
	
}
