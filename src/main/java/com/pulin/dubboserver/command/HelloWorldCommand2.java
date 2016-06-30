package com.pulin.dubboserver.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class HelloWorldCommand2 extends HystrixCommand<String> {  
	
  

	private  String name; 
	 
    

	protected HelloWorldCommand2(HystrixCommandGroupKey group) {
		super(group);
	}
	
	protected HelloWorldCommand2(com.netflix.hystrix.HystrixCommand.Setter sett) {
		super(sett);
	}

	 

    
    @Override  
    protected String run() throws Exception {  
        return "HystrixThread:" + Thread.currentThread().getName();  
    }  
    
    public static void main(String[] args) throws Exception{  
        HelloWorldCommand command = new HelloWorldCommand("semaphore");  
        String result = command.execute();  
        System.out.println(result);  
        System.out.println("MainThread:" + Thread.currentThread().getName());  
    }  
}  
/** 运行结果 
 HystrixThread:main 
 MainThread:main 
*/  
