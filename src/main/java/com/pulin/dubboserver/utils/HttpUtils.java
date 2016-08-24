package com.pulin.dubboserver.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * @author pul@shishike.com
 * @Time 2015年11月4日 下午5:22:38
 * http post 请求工具类
 */
public class HttpUtils {
	
	private static PoolingHttpClientConnectionManager cm;
	
	private static Gson gson = new Gson();
	
	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	private  static PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
		if (cm == null) {
			synchronized (HttpUtils.class) {
				cm = new PoolingHttpClientConnectionManager();
				cm.setMaxTotal(200);
				cm.setDefaultMaxPerRoute(200);
			}
		}
		return cm;
	}

	
	private static CloseableHttpClient getCloseableHttpClient() {
		return HttpClients.custom().setConnectionManager(getPoolingHttpClientConnectionManager()).build();
	}

	/**
	 * 大众点评 http post请求
	 * @param url (请求的url)
	 * @param token (token)
	 * @param encoded (已加密的内容)
	 * @param version (请求点评服务的版本号)
	 * @return 点评反馈的数据(UTF-8)
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws HttpException
	 */
	public static Map<String,Object> dianpingHttpPost(String url, String token, String encoded, String version) throws IOException, HttpException {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		CloseableHttpResponse response = null;
		try{
			if (StringUtils.isBlank(url) || StringUtils.isBlank(token) || StringUtils.isBlank(encoded)) {
				throw new IllegalArgumentException("请求参数有误");
			}
			if (StringUtils.isBlank(version)) {
				version = "v1.0.0";
			}
			CloseableHttpClient httpClient = getCloseableHttpClient();
			HttpPost httppost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
			httppost.setConfig(requestConfig);
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			NameValuePair param1 = new BasicNameValuePair("token", token);
			pairs.add(param1);
			NameValuePair param2 = new BasicNameValuePair("content", encoded);
			pairs.add(param2);
			NameValuePair param3 = new BasicNameValuePair("version", version);
			pairs.add(param3);
			HttpEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
			httppost.setEntity(entity);
			response = httpClient.execute(httppost);
			int statusCode = response.getStatusLine().getStatusCode();
			resultMap.put("code", statusCode);
			if (statusCode == 200) {
				HttpEntity responseEntity = response.getEntity();
				String responseStr = EntityUtils.toString(responseEntity, "UTF-8");
				resultMap.put("content", responseStr);
				return resultMap;
			} else {
				HttpEntity responseEntity = response.getEntity();
				String responseStr = EntityUtils.toString(responseEntity, "UTF-8");
				resultMap.put("content", responseStr);
				return resultMap;
			}
		}catch(Exception e){
			e.printStackTrace();
			resultMap.put("content", e.getMessage());
			resultMap.put("code", 500);
		}finally{
			if(response != null){
			   try {  
                    response.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
			}
		}
		return resultMap;
	}

	/**
	 * 点评上传图片
	 * @param url
	 * @param token
	 * @param encoded
	 * @param isFieldName
	 * @param version
	 * @return
	 * @throws IOException
     * @throws HttpException
     */
	public static Map<String,Object> dianpingHttpPostPhoto(String url, String token, String encoded, String fileUrl, String isFieldName, String version) throws IOException, HttpException {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		CloseableHttpResponse downLoadResponse = null;
		CloseableHttpResponse uploadResponse = null;
		try{
			if (StringUtils.isBlank(url) || StringUtils.isBlank(token) || StringUtils.isBlank(encoded)) {
				throw new IllegalArgumentException("请求参数有误");
			}
			if (StringUtils.isBlank(version)) {
				version = "v1.0.0";
			}
			CloseableHttpClient httpClient = getCloseableHttpClient();
			HttpGet httpGet = new HttpGet(fileUrl);
			downLoadResponse = httpClient.execute(httpGet);
			HttpEntity getEntity = downLoadResponse.getEntity();
			InputStream is = getEntity.getContent();
			Header contentType = getEntity.getContentType();

			HttpPost httppost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
			httppost.setConfig(requestConfig);

			HttpEntity entity = MultipartEntityBuilder
					.create()
					.addTextBody("token", token)
					.addTextBody("content", encoded)
					.addTextBody("version", version)
					.addBinaryBody(isFieldName, is, ContentType.create(contentType.getValue()), isFieldName)
					.build();

			httppost.setEntity(entity);
			uploadResponse = httpClient.execute(httppost);
			int statusCode = uploadResponse.getStatusLine().getStatusCode();
			resultMap.put("code", statusCode);
			HttpEntity responseEntity = uploadResponse.getEntity();
			String responseStr = EntityUtils.toString(responseEntity, "UTF-8");
			resultMap.put("content", responseStr);
			return resultMap;
		}catch(Exception e){
			throw e;
		}finally{
			if(downLoadResponse != null){
				try {
					downLoadResponse.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(uploadResponse != null){
				try {
					uploadResponse.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	
	/**
	 * @param url (请求的url)
	 * @param data 需要提交的数据 字符串
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws HttpException
	 */
	public static void httpPostByString(String url,String data){
		
		int statusCode = 500;
		CloseableHttpResponse response = null;
		try{
			if (StringUtils.isBlank(url)) {
				throw new IllegalArgumentException("请求参数有误,url不能为空");
			}
			CloseableHttpClient httpClient = getCloseableHttpClient();
			HttpPost httppost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
			httppost.setConfig(requestConfig);
			
			HttpEntity entity = new StringEntity(data,ContentType.APPLICATION_JSON);
			httppost.setEntity(entity);
			
			response = httpClient.execute(httppost);
			statusCode = response.getStatusLine().getStatusCode();//http解析状态码 200表示成功，其他表示失败
			HttpEntity responseEntity = response.getEntity();
			String responseStr = EntityUtils.toString(responseEntity, "UTF-8");	
			logger.warn("responseStr:"+responseStr);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(response != null){
			   try {  
                    response.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
			}
		}
	}

	
	
	public static String httpPost(String url,Map<String, Object> data) throws ClientProtocolException, IOException, HttpException {
		CloseableHttpResponse response = null;
		try{
			if (StringUtils.isBlank(url)) {
				throw new IllegalArgumentException("请求参数有误,url不能为空");
			}
			CloseableHttpClient httpClient = getCloseableHttpClient();
			HttpPost httppost = new HttpPost(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();//设置请求和传输超时时间
			httppost.setConfig(requestConfig);
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			Iterator<String> keys = data.keySet().iterator();
			StringBuilder sb = new StringBuilder();
			while(keys.hasNext()){
				String key = keys.next();
				Object value = data.get(key);
				//logger.info(key+":"+value);
				sb.append("&").append(key).append("=").append(value);
				NameValuePair param = new BasicNameValuePair(key, value.toString());
				pairs.add(param);
			}
			logger.info("httppostdata:"+sb.toString());
			HttpEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
			httppost.setEntity(entity);
			response = httpClient.execute(httppost);
			
			int statusCode = response.getStatusLine().getStatusCode();//http解析状态码 200表示成功，其他表示失败
			if(statusCode == 200){
				HttpEntity responseEntity = response.getEntity();
				String responseStr = EntityUtils.toString(responseEntity, "UTF-8");
				return responseStr;	
			}
			return null;	
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(response != null){
			   try {  
                    response.close();  
                } catch (IOException e) {
                    e.printStackTrace();  
                }  
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		String url = "http://10.47.55.41:9877/api/errorlog/order/query";
		
		//url = "http://10.47.55.41:9877/api/order/errorlog/test";
		//url = "http://172.17.17.130:9877/api/errorlog/order/query";
		url = "http://dev.partner.shishike.com/api/errorlog/order/query";
		
		OrderErrorRequest request = new OrderErrorRequest();
		//request.setShopId("247900001");
		// Due to limitations of the com.mongodb.BasicDBObject, 
		//you can't add a second 'timestamp' expression specified as 
		//'timestamp : { "$lte" : 1468290154601}'. Criteria already contains 'timestamp : 
		//{ "$gte" : 1468289457392}'.

		request.setStartTime(1468289457392L);
		request.setEndTime(1468290154601L);
		//request.setCondition(2);
		//request.setContent("下单");
		//request.setShopId("");
		request.setPageNo(1);
		request.setPageSize(5);
		
		
		//String data = JSON.toJSONString(Maps.newHashMap());
		String data = JSON.toJSONString(request);
		System.out.println(data);
		
		OrderErrorRequest rr = JSON.parseObject(data,OrderErrorRequest.class);
		System.out.println(rr.getShopId());
		
		HttpUtils.httpPostByString(url, data);
		
//		request.setPageNo(1);
//		data = JSON.toJSONString(request);
//		HttpUtils.httpPostByString(url, data);
		
	/*	
		for(int i=0;i<100;i++){
			HttpUtils.httpPostByString(url, data);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		
	}
	
	

	

}
