package dubboserver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class ElasticsearchTest {
	
	  public static void main(String args[]) throws UnknownHostException {
	       query();
	  }
	  
	  public static void add(){
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
	        
	        //ElasticsearchTools.addDocuments(list, "lol", "lol");
	  }
	  
	  
	  
	  
	  
	  
	  public static void query(){

	            //精确查询
	            Map<Object, Object> queryMaps = new HashMap<>();
	            queryMaps.put("commercialName", "四店");
	            
	            //全文检索
	            Map<Object, Object> fullTextQueryMaps = new HashMap<>();
	            fullTextQueryMaps.put("commercialName", "四店");

	            //范围查询
	            List<Map<Object, Object>> rangeLists = new ArrayList<Map<Object, Object>>();
	            
	        /*    Map<Object, Object> rangeMaps = new HashMap<>();
	            rangeMaps.put("field", "id");
	            rangeMaps.put("from", "1");
	            rangeMaps.put("to", "100");
	            rangeLists.add(rangeMaps);*/
	            
	          /*  Map<Object, Object> rangeMaps1 = new HashMap<>();
	            rangeMaps.put("field", "id");
	            rangeMaps.put("from", "36");
	            rangeMaps.put("to", "39");
	            rangeLists.add(rangeMaps1);*/
	            
	            //排序
	            Map<Object, Object> sortMaps = new HashMap<>();
	            sortMaps.put("id", "ASC");
	            
	            //高亮显示字段
	            List<String> fields = new ArrayList<String>();
	            //fields.add("commercialName");
	            //fields.add("createDateTime");
	            //fields.add("modifyDateTime");
	            
	            List<Map<String, Object>> lists = queryDocuments("commercial", "commercial", 0, 10, rangeLists, queryMaps, sortMaps, fields, fullTextQueryMaps);
	        
	            for(Map<String, Object> t : lists){
	          	   //System.out.println(t.get("id")+","+t.get("commercialName")+","+t.get("createDateTime")+","+t.get("modifyDateTime")+","+t.get("commercialDesc"));
	            	System.out.println(t.get("id")+","+t.get("commercialName"));
	            	//System.out.println(JSON.toJSON(t));
	            }
	  }
	  
	  
	    public  static List<Map<String, Object>> queryDocuments(
	    		String index, 
	    		String type, 
	    		int from, 
	    		int size, 
	    		List<Map<Object, Object>> rangeLists, 
	    		Map<Object, Object> queryMaps, 
	    		Map<Object, Object> sortMaps, 
	    		List<String> fields, 
	    		Map<Object, Object> fullTextQueryMaps) {
	    	
	        try {
	            List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
	            
	            /** 下面这一段是构造bool嵌套，就是构造一个在满足精确查找的条件下，再去进行多字段的或者关系的全文检索 **/
	            //构造全文或关系的查询
	          /*  BoolQueryBuilder fullTextQueryBuilder = QueryBuilders.boolQuery();
	            if (fullTextQueryMaps != null) {
	                for (Object key : fullTextQueryMaps.keySet()) {
	                	fullTextQueryBuilder = fullTextQueryBuilder.should(QueryBuilders.matchQuery((String) key, fullTextQueryMaps.get(key)));
	                }
	            }
	            //构造精确的并且查询
	            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
	            if (queryMaps != null) {
	            	boolQueryBuilder = boolQueryBuilder.must(fullTextQueryBuilder);
	                for (Object key : queryMaps.keySet()) {
	                	boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery((String) key, queryMaps.get(key)));
	                }
	            }*/
	            
	            /** 上面这一段是构造bool嵌套，就是构造一个在满足精确查找的条件下，再去进行多字段的或者关系的全文检索 **/
	            //match全文检索，但是并且的关系， 或者的关系要用
	            MatchQueryBuilder tq = null;
	            if (queryMaps != null) {
	                for (Object key : queryMaps.keySet()) {
	                    tq = QueryBuilders.matchQuery((String) key, queryMaps.get(key));
	                }
	            }

	            //term是代表完全匹配，即不进行分词器分析，文档中必须包含整个搜索的词汇
	              TermQueryBuilder tq2 = null;
	                if (queryMaps != null) {
	                    for (Object key : queryMaps.keySet()) {
	                        tq2 = QueryBuilders.termQuery((String) key, queryMaps.get(key));
	                    }
	                }
	            
	            

	            //构造范围查询参数
	            QueryBuilder rangeQueryBuilder = null;
	            if (rangeLists != null && rangeLists.size() > 0) {

	                for (Map<Object, Object> map : rangeLists) {
	                    if (map != null && (!map.isEmpty())) {
	                    	rangeQueryBuilder = QueryBuilders.rangeQuery( map.get("field").toString().trim() )
	                        		.from( map.get("from").toString().trim() )
	                        		.to( map.get("to").toString().trim() );
	                    }
	                }
	            }
	            
	            
	            //构造排序参数
	            SortBuilder sortBuilder = null;
	            if (sortMaps != null) {
	                for (Object key : sortMaps.keySet()) {
	                	sortBuilder = SortBuilders.fieldSort((String) key)
	                			.order(
	                					sortMaps.get(key).toString().trim().equals("ASC") ? SortOrder.ASC : SortOrder.DESC
	                					);
	                }
	            }

	            //构造查询
	            SearchRequestBuilder searchRequestBuilder = client()
	            		.prepareSearch(index)
	            		.setTypes(type)
	            		.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
	            		//.setQuery(fullTextQueryBuilder) // Query
	            		.setQuery(tq)
	                    .setPostFilter(rangeQueryBuilder) // Filter
	                    .setFrom(from)
	                    .setSize(size)
	                    .addSort(sortBuilder)
	                    .setExplain(true);
	            
	            //构造高亮字段
	            if (fields != null && fields.size() > 0) {
	                for (String field : fields) {
	                    searchRequestBuilder.addHighlightedField(field);
	                }
	                searchRequestBuilder.setHighlighterEncoder("UTF-8").setHighlighterPreTags("<span style=\"color:red\">").setHighlighterPostTags("</span>");
	            }

	            //查询
	            SearchResponse response = searchRequestBuilder.execute().actionGet();

	            //取值
	            SearchHits hits = response.getHits();
	            for (SearchHit hit : hits) {
	                Map<String, HighlightField> result = hit.highlightFields();

	                //用高亮字段替换搜索字段
	                for (String field : fields) {
	                    HighlightField titleField = result.get(field);
	                    if (titleField == null) {
	                        continue;
	                    }
	                    Text[] titleTexts = titleField.fragments();
	                    String value = "";
	                    for (Text text : titleTexts) {
	                        value += text;
	                    }
	                    hit.getSource().put(field, value);
	                }
	                
	                lists.add(hit.getSource());
	                
	                //System.out.println(hit.getSource());
	                //System.out.println(hit.getHighlightFields());
	                //System.out.println(hit.getSourceAsString());//json格式
	            }
	            
	            return lists;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;

	    }
	    
	public static Client client() {

		Client client = null;
		try {
			client = TransportClient.builder().build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.71.128"), 9300));
			// client.settings().settingsBuilder().put("cluster.name", "xxx");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return client;
	}
		
		
		
}
