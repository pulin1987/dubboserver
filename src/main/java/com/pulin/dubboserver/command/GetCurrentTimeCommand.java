package com.pulin.dubboserver.command;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
public class GetCurrentTimeCommand extends  HystrixCommand<Long>{
	
	private static long currentTimeCache;
	
	protected GetCurrentTimeCommand(Long s){
		

		
		//线程池/信号:ThreadPoolKey
		
		super(
				Setter.withGroupKey( HystrixCommandGroupKey.Factory.asKey("ExampleGroup") )
		        .andCommandKey( HystrixCommandKey.Factory.asKey("HelloWorld") )
		        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("HelloWorldPool"))
		        .andCommandPropertiesDefaults( null )
		);
		
		/*	 HystrixCommandSetter.WithGroupKey("TimeGroup")
		.AndCommandKey("GetCurrentTime")
		.AndCommandPropertiesDefaults(new HystrixCommandPropertiesSetter().WithExecutionIsolationThreadTimeout(TimeSpan.FromSeconds(2.0)).WithExecutionIsolationThreadInterruptOnTimeout(true));
	*/
		
		//线程池/信号:ThreadPoolKey
//		 super(
//				 Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
//	                .andCommandKey(HystrixCommandKey.Factory.asKey("HelloWorld"))
//	                /* 使用HystrixThreadPoolKey工厂定义线程池名称*/
//	                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("HelloWorldPool"))
//	                
//		);
	
	}


	protected GetCurrentTimeCommand(HystrixCommandGroupKey group) {
		super(group);
	}
	
	protected GetCurrentTimeCommand(com.netflix.hystrix.HystrixCommand.Setter setter) {
		super(setter);
	}
	
	
	
	
	

	@Override
	protected Long run() throws Exception {

		return currentTimeCache;

	}
	
	@Override
	protected  Long getFallback(){

		return currentTimeCache;

	}

}
