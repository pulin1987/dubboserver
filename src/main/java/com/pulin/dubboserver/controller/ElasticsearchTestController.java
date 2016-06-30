package com.pulin.dubboserver.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pulin.dubboserver.dao.CommercialDao;
import com.pulin.dubboserver.service.ElasticsearchService;
@Controller()
@RequestMapping("/api/elastic/test")
public class ElasticsearchTestController {
	
	@Autowired(required=false)
	private ElasticsearchService elasticsearchService;
	@Autowired(required=false)
	private CommercialDao commercialDao;

	@RequestMapping("/add")
	@ResponseBody
	public String test() {
		add();
		return "add:"+System.currentTimeMillis();
	}
	
	@RequestMapping("/query")
	@ResponseBody
	public String test2() {
		query();
		return "query:"+System.currentTimeMillis();
	}
	

	private void add(){
	    //updateDocument("member", "user", "1", "message", "我真的爱过啊！");
        //getDocuments("member", "user", "1", "2"); 
        //批量新增方法
        List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
       
        
        Map<Object, Object> map = new HashMap<Object, Object>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String pattern = "yyyy-MM-dd'T'HH:mm:ss:SSSZZ";
        System.out.println(DateFormatUtils.format(new Date(), pattern));
        map.put("id", "1");
        map.put("desc", "我们是共产主义接班人");
        map.put("name", "小名");
        map.put("type", "1");
        map.put("age", "36");
        map.put("mydate", df.format(new Date()));
        map.put("birthday", DateFormatUtils.format(new Date(), pattern));
        map.put("love", "足球,自行车,吉他");
        
        Map<Object, Object> map1 = new HashMap<Object, Object>();
        map1.put("id", "2");
        map1.put("desc", "我们是资本主义的接班人");
        map1.put("name", "小芳");
        map1.put("type", "12");
        map1.put("age", "32");
        map1.put("birthday", df.format(new Date()));
        map1.put("love", "足球,滑板,汽车");
        
        Map<Object, Object> map2 = new HashMap<Object, Object>();
        map2.put("id", "3");
        map2.put("name", "大豆");
        map2.put("type", "123");
        map2.put("desc", "我哎打球");
        map2.put("age", "31");
        map2.put("birthday", df.format(new Date()));
        map2.put("love", "航模,秋千,汽车");
        
        Map<Object, Object> map3 = new HashMap<Object, Object>();
        map3.put("id", "4");
        map3.put("name", "阿信");
        map3.put("type", "2");
        map3.put("desc", "我喜欢打篮球");
        map3.put("age", "21");
        map3.put("birthday", DateFormatUtils.format(new Date(), pattern));
        map3.put("love", "摩托,拼图,汽车");
        
        Map<Object, Object> map4 = new HashMap<Object, Object>();
        map4.put("id", "5");
        map4.put("name", "阿信");
        map4.put("type", "2");
        map4.put("desc", "我喜欢打篮球");
        map4.put("age", "21");
        map4.put("birthday", DateFormatUtils.format(new Date(), pattern));
        map4.put("love", "摩托,拼图,汽车");
        
        Map<Object, Object> map5 = new HashMap<Object, Object>();
        map5.put("id", "6");
        map5.put("name", "阿信");
        map5.put("type", "2");
        map5.put("desc", "我喜欢打篮球");
        map5.put("age", "21");
        map5.put("birthday", DateFormatUtils.format(new Date(), pattern));
        map5.put("love", "摩托,拼图,汽车");
        list.add(map);
        list.add(map3);
        /*list.add(map1);
        list.add(map2);
        list.add(map4);
        list.add(map5);*/
        elasticsearchService.addDocuments(list, "lol", "lol");
	}
	
	private void query(){
	      //测试查询方法
		  //1
          Map<Object, Object> queryMaps = new HashMap<>();
          queryMaps.put("type", "1");
          //2
          Map<Object, Object> fullTextQueryMaps = new HashMap<>();
          fullTextQueryMaps.put("name", "小");
          
          //3
          List<Map<Object, Object>> rangeLists = new ArrayList<Map<Object, Object>>();
          
          Map<Object, Object> rangeMaps = new HashMap<>();
          rangeMaps.put("field", "age");
          rangeMaps.put("from", "10");
          rangeMaps.put("to", "35");
          rangeLists.add(rangeMaps);
          
          Map<Object, Object> rangeMaps1 = new HashMap<>();
          rangeMaps.put("field", "age");
          rangeMaps.put("from", "36");
          rangeMaps.put("to", "39");
          rangeLists.add(rangeMaps1);
          
          //4
          Map<Object, Object> sortMaps = new HashMap<>();
          sortMaps.put("age", "ASC");
          
          //5
          List<String> fields = new ArrayList<String>();
          fields.add("name");
          fields.add("desc");
          
          List<Map<String, Object>> lists = elasticsearchService.queryDocuments("lol", "lol", 0, 10, rangeLists, queryMaps, sortMaps, fields, fullTextQueryMaps);
          System.out.println(lists.size());
          for(Map<String, Object> map : lists){
        	  System.out.println(map.toString());
          }
	
	}
	
	

}
