package com.pulin.dubboserver.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pulin.dubboserver.dao.CommercialDao;
import com.pulin.dubboserver.exception.DaoException;
import com.pulin.dubboserver.service.ElasticsearchService;
import com.pulin.dubboserver.to.Commercial;
@Controller()
@RequestMapping("/api/elastic")
public class ElasticsearchController {
	
	@Autowired(required=false)
	private ElasticsearchService elasticsearchService;
	@Autowired(required=false)
	private CommercialDao commercialDao;

	@RequestMapping("/add")
	@ResponseBody
	public String test() throws DaoException {
		add();
		return "add:"+System.currentTimeMillis();
	}
	
	@RequestMapping(value="/query",method=RequestMethod.GET, produces="text/plain;charset=UTF-8")
	@ResponseBody
	public String test2(HttpServletRequest request) {
		String name = request.getParameter("name");
		query(name);
		return "query:"+System.currentTimeMillis();
	}
	
	private void add() throws DaoException{
		 System.out.println("start...");
		 long s = System.currentTimeMillis();
		Boolean flag = Boolean.TRUE;
		int pageNo = 1;
		while(flag){
			 List<Commercial> commercials = commercialDao.queryAll(pageNo, 100);
			 if(commercials==null || commercials.size()<1){
				 flag = Boolean.FALSE;
				 break;
			 }
				
		     List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
			 commercials.forEach(c->{
				 Map<Object, Object> map = new HashMap<Object, Object>();
				 buildMap(c,map);
				 list.add(map);
			 });
			 elasticsearchService.addDocuments(list, "commercial", "commercial");
			 long e = System.currentTimeMillis();
			 System.out.println("end..."+(e-s));
			 pageNo++;
		}
	}
	
    private void buildMap(Object obj,Map<Object, Object> map) {
        Class<?> clazz = obj.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field[] fs = clazz.getDeclaredFields();
                for (int i = 0; i < fs.length; i++) {
                    Field f = fs[i];
                    f.setAccessible(true); // 设置些属性是可以访问的
                    String name = f.getName();
                    if (name.equalsIgnoreCase("serialVersionUID")) {
                        continue;
                    }
                    // 得到此属性的值
                    Object value = f.get(obj);
                    if (value != null) {
                    	if(name.equalsIgnoreCase("commercialid")){
                    		 map.put("id", value);
                    	}else{
                    		 map.put(name, value);
                    	}
                      
                    }
                    f.setAccessible(false);
                }
            } catch (Exception e) {
                //logger.error("mongodb update data buildUpdate :{}", e);
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }//for end
    }
	
	
	
	private void query(String name){
	      //测试查询方法
		  //1
          Map<Object, Object> queryMaps = new HashMap<>();
          queryMaps.put("commercialName", name);
          //2
          Map<Object, Object> fullTextQueryMaps = new HashMap<>();
          fullTextQueryMaps.put("commercialName", name);
          
          //3
          List<Map<Object, Object>> rangeLists = new ArrayList<Map<Object, Object>>();
          
          Map<Object, Object> rangeMaps = new HashMap<>();
          
          Map<Object, Object> rangeMaps1 = new HashMap<>();
          
          //4
          Map<Object, Object> sortMaps = new HashMap<>();
          sortMaps.put("commercialID", "ASC");
          
          //5
          List<String> fields = new ArrayList<String>();
          fields.add("commercialName");
          fields.add("commercialAdress");
          
          List<Map<String, Object>> lists = elasticsearchService.queryDocuments("commercial", "commercial", 0, 10, rangeLists, queryMaps, sortMaps, fields, fullTextQueryMaps);
          for(Map<String, Object> map : lists){
        	  System.out.println(map.toString());
          }
	
	}
	
	

}
