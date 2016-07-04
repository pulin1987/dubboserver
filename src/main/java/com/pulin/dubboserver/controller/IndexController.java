package com.pulin.dubboserver.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pulin.dubboserver.aop.OrderLogService;
import com.pulin.dubboserver.aop.ParamHolder;
import com.pulin.dubboserver.command.MyHystrixCommand;
@Controller
public class IndexController {
	
	@Autowired
	OrderLogService orderLogService;
	
	@RequestMapping("/api/index")
	@ResponseBody
	public String index(HttpServletRequest request) throws Throwable {
		
		for(int i=0;i<10;i++){
			ParamHolder.setParam("a"+i, "123456789kkk"+i);
		}
		
		
		orderLogService.test();
		return new MyHystrixCommand("index").execute();
	}
}
