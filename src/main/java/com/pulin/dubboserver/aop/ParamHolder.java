package com.pulin.dubboserver.aop;

import java.util.HashMap;
import java.util.Map;

public class ParamHolder {
	 
  private static final ThreadLocal< Map<String,String> > holder = new ThreadLocal< Map<String,String> >();

 
   public static void setParam(String key,String value) {
	   Map<String,String> map =  holder.get();
	   if(map != null){
		   map.put(key, value);
	   }else{
		   map = new HashMap<String,String>();
		   map.put(key, value);
	   }
	   holder.remove();
	   holder.set(map);
   }

 
   public static void clear() {
	   holder.remove();
   }

   
   public static Map<String,String> get() {
       return holder.get();
   }
}
