package com.pulin.dubboserver.command;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import rx.Observable;

public class MyHystrixCommand extends HystrixCommand<String> {
	private String name;
	public MyHystrixCommand(String name) {
		// 线程池/信号:ThreadPoolKey
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("MyHystrixCommandGroup"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("MyHystrixCommand"))
				/* 使用HystrixThreadPoolKey工厂定义线程池名称 */
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("MyHystrixCommandPool"))
				// 设置超时时间
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionTimeoutInMilliseconds(1000)
						.withExecutionIsolationSemaphoreMaxConcurrentRequests(100))
				// 设置核心线程数
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
						.withCoreSize(5)
						)
				);
			
		this.name = name;
	}

	//封装需要执行的逻辑代码
	@Override
	protected String run() throws Exception {
		// sleep 1 秒,调用会超时
		TimeUnit.MILLISECONDS.sleep(1000);
		return "MyHystrixCommand  "+name+"thread:" + Thread.currentThread().getName();
	}

	//封装降级逻辑
	@Override
	protected String getFallback() {
		return "exeucute Falled";
	}

	public static void main(String[] args) throws Exception {
		String s = new MyHystrixCommand("pulin").execute();
		System.out.println(s);

		Future<String> s2 = new MyHystrixCommand("pulin").queue();
		System.out.println(s2.get());

		Observable<String> s3 = new MyHystrixCommand("pulin").observe();
		System.out.println(s3.toString());

	}

}
