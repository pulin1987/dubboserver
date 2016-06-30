package com.pulin.dubboserver;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.bind.RelaxedNames;

public class Test {
	
	public static void main(String[] args) throws IOException {
	/*	String s = org.apache.commons.io.FileUtils.readFileToString(new File("D:/test.txt"));
		System.out.println(s);
		Map<String,String> param = new HashMap<String,String>();
		String[] a = s.split("&");
		for(String t : a){
			//System.out.println(t);
			String[] b = t.split("=");
			
			if(b.length ==2){
				String key = b[0];
				String value = b[1];
				param.put(key, value);
			}else{
				String key = b[0];
				param.put(key, "");
			}
			
		}
		
		param.remove("sig");
		
		//sig=0fe810628f8f354eb14efaf88c54b503
	    String uri = "http://dev.partner.shishike.com/api/cater/takeout/meituan/create/order";
		
	    String k = MeituanSignUtils.sortParam(param);
		
		k = k+"955d2565a81c1b89484edf9fd89c43f7";
		String url = uri+"?"+k;
		System.out.println(url);
		String md5 = MeituanSignUtils.getMD5Str(url);
		System.out.println(md5);*/
		
		
		String b = "@#$%^&   好久不见，非常感谢您选择最M音乐扒房并成为我们的会员，针对会员新推出优惠活动，第一:周六单点牛扒全场6.8折优惠；第二:会员天天享一款半价菜品；第三:会员续充，返充值金额10%的代金券不限消费金额，在餐厅当现金使用；第四:会员购买餐厅新鲜腌制的半成品牛扒享8.8折优惠哦！近期店内正在进行68元享220克加拿大深海三文鱼的活动，欢迎到店品尝！";
		b = "abcdef%";
		String bb = URLEncoder.encode(b,"gbk");
		System.out.println(bb);
		//bb="abcdef%25%25";
		System.out.println(URLDecoder.decode(bb,"gbk"));
		
		
		/*long s = 22577000L;
		System.out.println(s/1000/60/60);
		
		tt();*/
	}
	
	public static void tt(){
		RelaxedNames r = new RelaxedNames("/a_b.c");
		for(String key:r){
			System.out.println(key);
		}
	}
	
	   static class Person2{
	    	private String name;
	    	private int age;
	    	private double salar;
	    	private String birthday;
	    	private boolean isSingle;
	    	public static final String COUNTYR = "China";
	    	
	    }
	   
	   static class Person implements Serializable{
		    private static Person person;
	    	
		    private String name;
	    	
		    private int age;
	    	
	    	private Person(){
	    		
	    	}
	    	
	    	public static Person getInstance(){
	    		if(person == null){
	    			synchronized (Person.class) {
	    				person = new Person();
						return person;	
					}
				}
				return person;
	    	}
	    	
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public int getAge() {
				return age;
			}
			public void setAge(int age) {
				this.age = age;
			}
			
			public void eat(String foodName){
				System.out.println(name+" eat "+foodName);
			}
			
			public static void main(String[] args) {
				
				//启动10个线程
//				for(int i=0;i<10;i++){
//					 new Thread(new Runnable(){
//							public void run() {
//								try {
//									System.out.println(Thread.currentThread().getName()+"开始运行");
//									//睡眠1秒
//									Thread.sleep(1000);
//									System.out.println(Thread.currentThread().getName()+"运行结束");
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//							}
//						}).start();
//				}
				
				
				
				
			
			}
			
			
			/**
			 * 找出文件夹中的所有文件，存入 list集合中
			 * @param file
			 * @param list
			 * @return
			 */
			public List<String> findFile(File file,List<String> list){
		        File[] files = file.listFiles();
		        if(files == null){
		        	return list;// 判断目录下是不是空的
		        }
		        for (File f : files) {
		            if(f.isDirectory()){// 判断是否文件夹
		            	list.add(f.getPath());
		            	findFile(f,list);// 调用自身,查找子目录
		            }else
		            	list.add(f.getPath());
		        }
		        return list;
		    }
			
			
	    	
	    }

}
