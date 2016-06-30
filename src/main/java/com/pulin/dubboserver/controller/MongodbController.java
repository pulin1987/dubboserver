package com.pulin.dubboserver.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pulin.dubboserver.dao.MongoDBOrderAutoCompleteDao;
import com.pulin.dubboserver.to.MongoDBOrderAutoCompleteTO;
@Controller
public class MongodbController {
	
	@Autowired(required=false)
    private MongoDBOrderAutoCompleteDao mongoDBOrderAutoCompleteDao;
	
	@RequestMapping("/api/mongodb/save")
	@ResponseBody
	public String save() {
		long s = System.currentTimeMillis();
		for(long i=1;i<=1000;i++){
			MongoDBOrderAutoCompleteTO to = new MongoDBOrderAutoCompleteTO();
			to.setOrderId(1+"");
			to.setTradeId(i);
			Date d = new Date();
			to.setCreateTime(d);
			to.setModifyTime(d);
			to.setExpireTime(new Date(d.getTime()+3600*1000));
			to.setShopId(247900001L);
			to.setSource(16);
			to.setStatus(0);
			mongoDBOrderAutoCompleteDao.save(to);
		}
		 long e = System.currentTimeMillis();
         System.out.println( (e-s) );
		return "save success:"+System.currentTimeMillis();
	}
	
	
	@RequestMapping("/api/mongodb/find")
	@ResponseBody
	public String find() {
		 long s = System.currentTimeMillis();
		 List<MongoDBOrderAutoCompleteTO> list = mongoDBOrderAutoCompleteDao.findList(0);
		 list.forEach(to -> {
	           System.out.println(to.toString());
	     });
		 
		 long e = System.currentTimeMillis();
		return "find success:"+(e-s);
	}
	
	@RequestMapping("/api/mongodb/find2")
	@ResponseBody
	public String find2(HttpServletRequest request) {
		int pageNo=1;
		int pageSize=10;
		String noStr = request.getParameter("pageNo");
		if(noStr != null){
			pageNo = Integer.parseInt(noStr);
		}
		 long s = System.currentTimeMillis();
		 long count = mongoDBOrderAutoCompleteDao.count(0);
		 List<MongoDBOrderAutoCompleteTO> list2 = mongoDBOrderAutoCompleteDao.findList2(0,pageNo,pageSize);
		 list2.forEach(to -> {
	           System.out.println(to.toString());
	     });
		 long e = System.currentTimeMillis();
		return "find success:"+(e-s);
	}
	
	@RequestMapping("/api/mongodb/count")
	@ResponseBody
	public String count() {
		 long s = System.currentTimeMillis();
		 Long num = mongoDBOrderAutoCompleteDao.count(0);
		 long e = System.currentTimeMillis();
		return "find success:"+(e-s)+",num:"+num;
	}
}
