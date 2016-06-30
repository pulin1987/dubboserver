package com.pulin.dubboserver.common;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author pulin
 *
 */
public class DynamicDataSourceAspect {
	/**
	 * 前置处理
	 * @param point
	 */
	public void beforeService(JoinPoint point){
		Method method=((MethodSignature)point.getSignature()).getMethod();
		boolean  isTransactional= method.isAnnotationPresent(Transactional.class);
		if(isTransactional){
			DynamicDataSourceHolder.setMasterDataSource();
		}
	}

	/**
	 * master
	 * @param point
	 */
	public void setMasterDataSource(JoinPoint point){
		DynamicDataSourceHolder.setMasterDataSource();
	}

	/**
	 * slave
	 * @param point
	 */
	public void setSlaveDataSource(JoinPoint point){
		DynamicDataSourceHolder.setSlaveDataSource();
	}
}
