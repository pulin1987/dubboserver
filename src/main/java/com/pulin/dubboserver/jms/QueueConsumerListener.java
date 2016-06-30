package com.pulin.dubboserver.jms;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueConsumerListener implements MessageListener {
	final static Logger logger = LoggerFactory.getLogger(QueueConsumerListener.class);
	public void onMessage(Message message) {
		if(message instanceof TextMessage){
			 TextMessage msg = (TextMessage) message;
			 logger.info("gateway-jms-cosumer-test,msg:{}",msg.toString());
		}
		
	}

}
