package com.pulin.dubboserver.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;



/**
 * solrCloud索引操作(增加 删除 查询)
 * @author MyPC
 *
 */
//@Service
public class SolrCloudQueryService {
	private static String zkHost = "192.168.71.128:2181,192.168.71.128:2182,192.168.71.128:2183";
	
	private static String defaultCollection = "default";
	
	
	private static Logger logger = LoggerFactory.getLogger(SolrCloudQueryService.class);
	
	private static CloudSolrClient client = initCloudSolrClient();
	
	private static CloudSolrClient initCloudSolrClient() {
		CloudSolrClient client = new CloudSolrClient(zkHost);
		client.setZkClientTimeout(20000);
		client.setZkConnectTimeout(10000);
		client.setDefaultCollection(defaultCollection);
		client.connect();
		return client;
	}
	

	
	 /**
	  * 添加一条数据的索引
	  * @param id
	  */
    public void addOne(String id){
      if(id == null){
    	return;
      }	
      //先删除
      deleteOne(String.valueOf(id));
      
      client.connect();
  	  Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>(); 
  	  SolrInputDocument doc = new SolrInputDocument();  
  		  doc.addField("id", "");
  	      doc.addField("commercialID", "");  
  	      doc.addField("commercialName", "");
  	      doc.addField("status", 0);
  	      String latlong = "";
	  		if(latlong == null){
	  			logger.error("维度范围数据为空");
				return;
			}
			if(latlong.indexOf(",") < 0){
				logger.error("维度范围数据不全，只有经度或者只有维度");
				return;
			}
			boolean flag = true;
			String[] ttt = latlong.split(",");
			String weidu = ttt[0];//维度
			double d_weidu = 0D;
			try{
				d_weidu = Double.parseDouble(weidu);
			}catch(Exception e){
				logger.error("commercialID:"+id);
				e.printStackTrace();
			
			}
			
			
			if(!(d_weidu>0 && d_weidu<90)){
				logger.error("维度范围异常[0-90],"+d_weidu);
				flag = false;
			}
			
			String jindu = ttt[1];//经度
			double d_jindu = Double.parseDouble(jindu);
			if( !(d_jindu>0 && d_jindu<180) ){
				logger.error("经度范围异常[0-180],"+d_jindu);
				flag = false;
			}
			
			if(flag){
				 doc.addField("latlong", "");
			}
  	     
  	        docs.add(doc);
  	  
  	    try {
			client.add(docs);
			client.optimize();//对索引进行优化 压缩
			client.commit(); 
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{

		}   
  }
    
   /**
    * 创建糯米排队有效商户数据的索引
    */
   public void addNuomiPoiIndex(){
      client.connect();
 	  List<Long> ids = new ArrayList<Long>();
 	
 	  String sql = "select * from commercial_order_setting c where c.order_source = 4 and c.is_delete  = ? ";
 	  List<String> list = Lists.newArrayList();
 	  
 	  Map<Long,Integer> statusMap = new HashMap<Long,Integer>();
 	
 	  //先删除
 	  deleteAll();
 	  
 	  Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
 		  SolrInputDocument doc = new SolrInputDocument();
 		  Long commercialID = 24790001L;
 		  doc.addField("id", commercialID);
 	      doc.addField("commercialID", commercialID);  
 	      doc.addField("commercialName", "");
 	      doc.addField("status",statusMap.get(""));
 	      String latlong = "";
			boolean flag = true;
			String[] ttt = latlong.split(",");
			String weidu = ttt[0];//维度
			double d_weidu = 0D;
			try{
				d_weidu = Double.parseDouble(weidu);
			}catch(Exception e){
				logger.error("commercialID:"+commercialID);
				e.printStackTrace();
			}
			
			if(!(d_weidu>0 && d_weidu<90)){
				logger.error("维度范围异常[0-90],"+d_weidu+",commercialID:"+commercialID);
				flag = false;
			}
			
			String jindu = ttt[1];//经度
			double d_jindu = Double.parseDouble(jindu);
			if( !(d_jindu>0 && d_jindu<180) ){
				logger.error("经度范围异常[0-180],"+d_jindu+",commercialID:"+commercialID);
				flag = false;
			}
			
			if(flag){
				 doc.addField("latlong", "");
			}
 	     
 	        docs.add(doc);
 	  
 	  try {
			client.add(docs);
			client.optimize();//对索引进行优化 压缩
			client.commit(); 
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			
		}   
 }
   
   public void getZKHost(){
         client.connect();
         try {
			String zkhost = client.getZkHost();
			logger.error("===zkhost===:"+zkhost);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		} 
   }
    
    /**
     * 根据ID删除索引
     * @param id
     */
    public void deleteOne(String id){
         client.connect();
         try {
			client.deleteById(id);
			client.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
		
		}
    }
    
    /**
     * 删除所有索引
     */
    public void deleteAll(){
        client.connect();
        try {
			client.deleteByQuery("*:*");
			client.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{

		}
   }
    
    /**
     * 查询数据
     * @param longitude 经度
     * @param latitude 维度
     * @param distance 距离
     * @param start 查询数据的开始位置
     * @param count 返回数据条数
     * @return 商户ID集合
     */
    public void query(Double longitude,Double latitude,Double distance,Integer start,Integer count){
    	SolrQuery query = new SolrQuery(); 
    	//query.set("q", "*:*");
    	query.set("q", "*:*");
    	//query.setQuery("status:0");
    	query.set("start", start);  //记录开始位置
    	query.set("rows", count+10);  //查询的行数
    	if(longitude  != null && latitude != null){
    		query.set("fq", "{!geofilt}");//距离过滤函数
    		query.setParam("pt", latitude+","+longitude);//当前  经纬度(维度,经度)
    		query.set("sfield", "latlong");//经纬度的字段
    		query.set("d", String.valueOf(distance));//就近 d km的所有数据
    		query.set("sort", "geodist() asc");  //根据距离排序：由近到远
    	}
    	query.setFields("id","name","latlong","_dist_:geodist()","score");//返回字段
    	
    	client.connect();
    	try {
			 QueryResponse response = client.query(query);
			 //搜索得到的结果数
			 Long countAll = response.getResults().getNumFound();
	         logger.debug("Find:" + countAll+"条数据");
			 for (SolrDocument doc : response.getResults()) {
				 Long commercialId = Long.parseLong(doc.getFieldValue("id").toString());//商户ID
				 String commercial_distance = doc.getFieldValue("_dist_").toString();//距离
	         }
    	} catch (SolrServerException e) {
			e.printStackTrace();
    	} catch (Exception e) {
			e.printStackTrace();
    	}finally{
    	
    	}
    	
    	
    }
    
}
