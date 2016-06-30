package com.pulin.dubboserver.command;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
/**
 * Servlet容器中，可以直接实用Filter机制Hystrix请求上下文
 * @author pulin
 *
 */
public class HystrixRequestContextServletFilter implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HystrixRequestContext context = HystrixRequestContext.initializeContext();
		try {
			chain.doFilter(request, response);
		} finally {
			context.shutdown();
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		

	}

	@Override
	public void destroy() {
		

	}
	
//	<filter>  
//    <display-name>HystrixRequestContextServletFilter</display-name>  
//    <filter-name>HystrixRequestContextServletFilter</filter-name>  
//    <filter-class>com.netflix.hystrix.contrib.requestservlet.HystrixRequestContextServletFilter</filter-class>  
//  </filter>  
//  <filter-mapping>  
//    <filter-name>HystrixRequestContextServletFilter</filter-name>  
//    <url-pattern>/*</url-pattern>  
// </filter-mapping>
}