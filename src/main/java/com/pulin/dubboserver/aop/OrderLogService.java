package com.pulin.dubboserver.aop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pulin.dubboserver.annotation.OrderLogRecord;
import com.pulin.dubboserver.exception.BizException;

/**
 * Created by pulin on 2016/7/1 0001.
 */
@Component
public class OrderLogService {
	/**
	 *
	 */
	public static Logger logger = LoggerFactory.getLogger(OrderLogService.class);
	
	@OrderLogRecord
	public void test() throws Throwable{
	/*	int n = 0;
		if(n==0){
			throw new BizException(0,"error 0");
		}else if(n==1){
			throw new BizException(1,"error 1");
		}else if(n==2){
			throw new BizException(2,"error 2");
		}else if(n==3){
			throw new BizException(3,"error 3");
		}*/
		System.out.println("=============");
	}

	
	public void execprint() throws Throwable {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> paramHolderMap = ParamHolder.get();
		
		Iterator<String> iter = paramHolderMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			map.put(key, paramHolderMap.get(key));
		}
		ParamHolder.clear();

		
		Iterator<String> iter2 = map.keySet().iterator();
		while (iter2.hasNext()) {
			String key = iter2.next();
			System.out.println("key:" + key + ",value:" + paramHolderMap.get(key));
		}

	}

}
