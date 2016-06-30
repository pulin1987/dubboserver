package com.pulin.dubboserver.jms;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.config.AbstractFactoryBean;

public class JmsListenerThreadFactoryBean extends AbstractFactoryBean<ExecutorService>{

	@Override
	public Class<?> getObjectType() {
		return ExecutorService.class;
	}

	@Override
	protected ExecutorService createInstance() throws Exception {
		return new ThreadPoolExecutor(
				2, 
				10,
                0L, 
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),new ThreadFactory() {
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setName("JMS-ConsumerListener-Thread-"+Thread.activeCount());
						return t;
					}
				},
                new DiscardOldestPolicy());
	}

}
