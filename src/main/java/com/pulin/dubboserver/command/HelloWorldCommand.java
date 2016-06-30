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
import rx.Observer;
import rx.functions.Action1;

public class HelloWorldCommand extends HystrixCommand<String> {
	
	private final String name;  
    
	public HelloWorldCommand(String name) {
		
		//最少配置:指定命令组名(CommandGroup) 
        // super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
		
		
		//依赖命名:CommandKey
		//super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
		/* HystrixCommandKey工厂定义依赖名称  */
		//.andCommandKey(HystrixCommandKey.Factory.asKey("HelloWorld")));   
       
	    
		// 依赖分组:CommandGroup
		// 使用HystrixCommandGroupKey工厂定义  
		// Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HelloWorldGroup"));
		 
		//线程池/信号:ThreadPoolKey
		 super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
	                .andCommandKey(HystrixCommandKey.Factory.asKey("HelloWorld"))
	                /*使用HystrixThreadPoolKey工厂定义线程池名称*/
	                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("HelloWorldPool"))
	                //设置超时时间
	                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(2000).withExecutionIsolationSemaphoreMaxConcurrentRequests(100) )
	                //设置核心线程数
	                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize (60))
				  );
		 
		this.name = name;
    }  
   
 
	
	//降级逻辑
	@Override
	protected String getFallback() {
		 return "exeucute Falled";  
	}
	
/*	@Override  
    protected String run() {  
        // 依赖逻辑封装在run()方法中  
        return "Hello " + name +" thread:" + Thread.currentThread().getName(); 
    } */
	
	@Override
	protected String run() throws Exception {
		// sleep 1 秒,调用会超时
		TimeUnit.MILLISECONDS.sleep(1000);
		return "Hello " + name + " thread:" + Thread.currentThread().getName();
	}

	public static void main(String[] args) throws Exception {
		
		String s = new HelloWorldCommand("pulin").execute();
		System.out.println(s);
		
		Future<String> s2 = new HelloWorldCommand("pulin").queue();
		System.out.println(s2.get());
		
		Observable<String> s3 = new HelloWorldCommand("pulin").observe();
		System.out.println(s3.toString());
	
	}
	
    //调用实例  
  /*  public static void main(String[] args) throws Exception{
       listener();  
    } */ 
    
    public static void s2()  throws Exception{
        //每个Command对象只能调用一次,不可以重复调用,  
        //重复调用对应异常信息:This instance can only be executed once. Please instantiate a new instance.  
        HelloWorldCommand helloWorldCommand = new HelloWorldCommand("Synchronous-hystrix");  
        //使用execute()同步调用代码,效果等同于:helloWorldCommand.queue().get();   
        String result = helloWorldCommand.execute();
        System.out.println("result=" + result);  
   
        helloWorldCommand = new HelloWorldCommand("Asynchronous-hystrix");  
        //异步调用,可自由控制获取结果时机,  
        Future<String> future = helloWorldCommand.queue();  
        //get操作不能超过command定义的超时时间,默认:1秒  
        result = future.get(100, TimeUnit.MILLISECONDS);  
        System.out.println("result=" + result);  
        System.out.println("mainThread=" + Thread.currentThread().getName());  
        
    	//运行结果: run()方法在不同的线程下执行  
	    // result=Hello Synchronous-hystrix thread:hystrix-HelloWorldGroup-1  
	    // result=Hello Asynchronous-hystrix thread:hystrix-HelloWorldGroup-2  
	    // mainThread=main 
    }
    
	public static void listener() {
		// 注册观察者事件拦截
		Observable<String> fs = new HelloWorldCommand("World").observe();
		// 注册结果回调事件
		fs.subscribe(new Action1<String>() {
			@Override
			public void call(String result) {
				// 执行结果处理,result 为HelloWorldCommand返回的结果
				// 用户对结果做二次处理.
			}
		});
		// 注册完整执行生命周期事件
		fs.subscribe(new Observer<String>() {
			@Override
			public void onCompleted() {
				// onNext/onError完成之后最后回调
				System.out.println("execute onCompleted");
			}

			@Override
			public void onError(Throwable e) {
				// 当产生异常时回调
				System.out.println("onError " + e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onNext(String v) {
				// 获取结果后回调
				System.out.println("onNext: " + v);
			}
		});
		
		/* 运行结果 
		call execute result=Hello observe-hystrix thread:hystrix-HelloWorldGroup-3 
		onNext: Hello observe-hystrix thread:hystrix-HelloWorldGroup-3 
		execute onCompleted 
		*/ 
	
	}
    
    
    
}


