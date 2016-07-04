package com.pulin.dubboserver.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pulin.dubboserver.annotation.OrderLogRecord;



/**
 * Created by pulin on 2016/7/1 0001.
 */
@Aspect
@Component
public class OrderLogAspect {
    /**
     *
     */
    public static Logger logger = LoggerFactory.getLogger(OrderLogAspect.class);
    
    @Autowired
    OrderLogService orderLogService;

    @Pointcut("@annotation(com.pulin.dubboserver.annotation.OrderLogRecord)")
    private void logRecordPointcut() {

    }




    /**
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    //@Around("logRecordPointcut()")
    public Object logRecordAround(ProceedingJoinPoint joinPoint) {
    	Object object = null;
		try {
			 Map<String, Object> map = getOrderLogRecord(joinPoint);
			  // Class returnType = getMethodByClassAndName(joinPoint.getTarget().getClass(),joinPoint.getSignature().getName()).getReturnType();
			  System.out.println("执行:"+map.get("method")+"开始");
		       object = joinPoint.proceed();
		       System.out.println("执行:"+map.get("method")+"结束");
		       orderLogService.execprint();
		} catch (Throwable e) {
			e.printStackTrace();
		}
       return object;
    }
    
    @After("logRecordPointcut()")
    public void doAfter() throws Throwable {
    	  orderLogService.execprint();
    }
    
    @AfterThrowing(pointcut = "logRecordPointcut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
    	 try {
			orderLogService.execprint();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
    	 
    	 
       /* try {
            StringBuffer params = new StringBuffer();
            Object[] objects = joinPoint.getArgs();
            if (objects != null && objects.length > 0) {
                for (Object object : objects) {
                    params.append(object.getClass().getName())
                            .append(" : ")
                            .append(JSON.toJSON(object))
                            .append(" | ");
                }
            }
            String message = getMethodName(joinPoint) + " : " + params.toString()+":"+JSON.toJSON(e);
           // logger.info("request parameters : {}  ", message);
            System.out.println(message);
        } catch (Exception ex) {
            // 记录本地异常日志
            //logger.error("aop throwing exception", e);
            System.out.println("aop throwing exception:"+JSON.toJSON(e));
        }*/
    }




    /**
     * 获取注解
     * @param joinPoint
     * @return
     * @throws Exception
     */
    protected Map<String, Object> getOrderLogRecord(JoinPoint joinPoint) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class<?> targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    map.put("orderLogRecord", method.getAnnotation(OrderLogRecord.class));
                    map.put("method", method);
                    return map;
                }
            }
        }
        return map;
    }

    /**
     * 根据类和方法名得到方法
     */
    public Method getMethodByClassAndName(Class<?> c, String methodName) {
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getReturnType() == Object.class) {
                return method;
            }
        }
        return null;
    }

    protected String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()";
    }
}
