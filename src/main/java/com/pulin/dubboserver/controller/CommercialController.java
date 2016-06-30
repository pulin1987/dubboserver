package com.pulin.dubboserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.pulin.dubboserver.dao.CommercialDao;
import com.pulin.dubboserver.exception.DaoException;
import com.pulin.dubboserver.to.Commercial;
@Controller
public class CommercialController {
	
	@Autowired(required=false)
	private CommercialDao commercialDao;


	
	//@RequestMapping("/api/commercial/query")
	@RequestMapping(value="/api/commercial/query",method=RequestMethod.GET, produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String test2() throws DaoException {
		StringBuffer sb = new StringBuffer();
		List<Commercial> list = commercialDao.queryAll();
		list.forEach(c->{
			//System.out.println(c);
			sb.append(c).append("\n");
		});
		
		return "query:"+sb.toString();
	}
	
	@RequestMapping(value="/api/commercial/query2",method=RequestMethod.GET, produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String test() throws DaoException {
		List<Commercial> list = commercialDao.queryAll();
		return JSON.toJSONString(list);
	}
	
	
	

}
