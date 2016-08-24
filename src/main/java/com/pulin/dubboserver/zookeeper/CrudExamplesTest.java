package com.pulin.dubboserver.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;

public class CrudExamplesTest {
	  
	    public static void main(String[] args) {
	       new Thread(new Job()).start();
	       new Thread(new Job()).start();
	       new Thread(new Job()).start();
	    } 
	    
	    
	   static class Job implements Runnable{
	    	private  final CuratorFramework client = ClientFactory.newClient(); 
	    	private  final String PATH = "/test";  
	    	
			@Override
			public void run() {
				  try {  
			            client.start();
			            String s = client.create().forPath(PATH, Thread.currentThread().getName().getBytes());
			            System.out.println("s:"+s);
			        } catch (Exception e) {
			           // e.printStackTrace();
			        	System.out.println(Thread.currentThread().getName());
			        } finally {  
			            CloseableUtils.closeQuietly(client);
			        }  
				
			}
	    	
	    }
	    
	    
}
