package com.pulin.dubboserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pulin.dubboserver.command.MyHystrixCommand;
@Controller
public class IndexController {
	
	@RequestMapping("/api/index")
	@ResponseBody
	public String index() {
		return new MyHystrixCommand("index").execute();
	}
}
