package com.pulin.dubboserver.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.DefaultSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 提供基于经纬度的数据查询 
 * @author MyPC
 *
 */

public class SolrQueryService {
	
	static Logger logger = LoggerFactory.getLogger(SolrQueryService.class);
	
	private static String URI = "http://192.168.71.128/solr/core1";

    private static HttpSolrServer server = null;
    
    static {
    	   server = new HttpSolrServer(URI);
    	   server.setSoTimeout(3000); // socket read timeout  
           server.setConnectionTimeout(1000);  
           server.setDefaultMaxConnectionsPerHost(1000);  
           server.setMaxTotalConnections(100);  
           server.setFollowRedirects(false); // defaults to false
           server.setAllowCompression(true);  
           server.setMaxRetries(1);  
    }


    /**
     * 
     * @param longitude 经度
     * @param latitude 维度
     * @param distance 距离
     * @param start 查询数据的开始位置
     * @param count 返回数据条数
     * @return 商户ID集合
     */
    public void query(Double longitude,Double latitude,Double distance,Integer start,Integer count){
    	SolrQuery query = new SolrQuery();   
    	query.set("q", "*:*");
    	query.set("start", start);  //记录开始位置
    	query.set("rows", count);  //查询的行数
    	if(longitude  != null && latitude != null){
    		query.set("fq", "{!geofilt}");//距离过滤函数
    		query.setParam("pt", latitude+","+longitude);//当前  经纬度(维度,经度)
    		query.set("sfield", "latlong");//经纬度的字段
    		query.set("d", String.valueOf(distance));//就近 d km的所有数据
    		query.set("sort", "geodist() asc");  //根据距离排序：由近到远
    	}
    	
    	query.setFields("id","name","latlong","_dist_:geodist()","score");//返回字段
    
    	
    	try {
        	 List<Long> commercialIDs = new ArrayList<Long>();
			 QueryResponse response = server.query(query);
			 server.query(query);
			 //搜索得到的结果数
			 Long countAll = response.getResults().getNumFound();
	         logger.debug("Find:" + countAll+"条数据");
			 for (SolrDocument doc : response.getResults()) {
				 Long commercialId = Long.parseLong(doc.getFieldValue("id").toString());
				 commercialIDs.add(commercialId);
	         }
    	} catch (Exception e) {
			e.printStackTrace();
    	}
    	
    	
    }
    
    
    public static void query(){
    	SolrQuery query = new SolrQuery();
    	
		query.set("q", "*:*");
		//query.set("q", "content:(公司 OR 希望)");
		
		
	/*	Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("auth","content:操蛋"); //将过滤查询语句放入参数里
		NamedList namedList = new NamedList(paramMap);
		SolrParams params = DefaultSolrParams.toSolrParams(namedList);
		query.add(params);
		query.addFilterQuery("{!dismax v=$auth}");//通过dismax转换为query，加入查询语句，大功告成
	 */
		
/*		Map<String,Object> paramMap = new HashMap<String,Object>();
		SearchParams params = new SearchParams(paramMap) ;
		paramMap.put("auth","filed4:1 filed4:3"); //将过滤查询语句放入参数里
		query.add(params);
		query.addFilterQuery("{!dismax v=$auth}");//通过dismax转换为query，加入查询语句，大功告成
*/		
		//query.addFilterQuery("content:(操蛋)");
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("auth","content:操蛋"); //将过滤查询语句放入参数里
		NamedList namedList = new NamedList(paramMap);
		SolrParams params = DefaultSolrParams.toSolrParams(namedList);
		query.add(params);
		//query.addFilterQuery("content:操蛋");
		try {
			QueryResponse response = server.query(query);
			server.query(query);
			for (SolrDocument doc : response.getResults()) {
				System.out.println(doc.toString());
			}
			
			server.commit();
			server.optimize();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
   public static void main(String[] args){
	   query();
   }
   

    


  
    
}
