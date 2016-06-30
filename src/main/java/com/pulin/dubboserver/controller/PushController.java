package com.pulin.dubboserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class PushController {
	
	@RequestMapping("/api/push/test")
	@ResponseBody
	public String index() {
		return "api_push_test:"+System.currentTimeMillis();
	}
}
