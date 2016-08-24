package com.pulin.dubboserver.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryOperators;
import com.mongodb.util.JSON;

public class MongodbUtils {
	
	public static Mongo getMongo(){
		Mongo mongo = null;
		try {
			mongo = new Mongo("192.168.71.128",27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return mongo;
	}
	
	public static void show() {
		Mongo mongo = getMongo();
		DB db = mongo.getDB("test");
		DBCollection users = db.getCollection("user");

		// 查询所有的Database
		for (String name : mongo.getDatabaseNames()) {
			System.out.println("dbName: " + name);
		}

		
		// 查询所有的聚集集合
		for (String name : db.getCollectionNames()) {
			System.out.println("collectionName: " + name);
		}

		

		// 查询所有的数据
		DBCursor cur = users.find();
		while (cur.hasNext()) {
			System.out.println(cur.next());
		}
		System.out.println(cur.count());
		System.out.println(cur.getCursorId());
		System.out.println(JSON.serialize(cur));

	}
	
	public static void add() {
		Mongo mongo = getMongo();
		DB db = mongo.getDB("test");
		DBCollection users = db.getCollection("user");
		    
	    DBObject user = new BasicDBObject();
	    user.put("name", "hoojo");
	    user.put("age", 24);
	    //users.save(user)保存，getN()获取影响行数
	    //print(users.save(user).getN());
	    
	  
	    
	    //扩展字段，随意添加字段，不影响现有数据
	    user.put("sex", "男");
	    users.save(user).getN();
	    //添加多条数据，传递Array对象
	    users.insert(user, new BasicDBObject("name", "tom")).getN();
	    //添加List集合
	    List<DBObject> list = new ArrayList<DBObject>();
	    list.add(user);
	    DBObject user2 = new BasicDBObject("name", "lucy");
	    user.put("age", 22);
	    list.add(user2);
	    //添加List集合
	    users.insert(list).getN();
	    
	    //查询下数据，看看是否添加成功
	   System.out.println("count: " + users.count());
	}
	
	
	public void remove() {
		Mongo mongo = getMongo();
		DB db = mongo.getDB("test");
		DBCollection users = db.getCollection("user");
	    System.out.println("删除id = 4de73f7acd812d61b4626a77：" + users.remove(new BasicDBObject("_id", new ObjectId("4de73f7acd812d61b4626a77"))).getN());
	    System.out.println("remove age >= 24: " + users.remove(new BasicDBObject("age", new BasicDBObject("$gte", 24))).getN());
	}
	
	
	public void modify() {
		Mongo mongo = getMongo();
		DB db = mongo.getDB("test");
		DBCollection users = db.getCollection("user");
		
		System.out.println("修改：" + users.update(new BasicDBObject("_id", new ObjectId("4dde25d06be7c53ffbd70906")), new BasicDBObject("age", 99)).getN());
		System.out.println("修改：" + users.update(
	            new BasicDBObject("_id", new ObjectId("4dde2b06feb038463ff09042")), 
	            new BasicDBObject("age", 121),
	            true,//如果数据库不存在，是否添加
	            false//多条修改
	            ).getN());
		System.out.println("修改：" + users.update(
	            new BasicDBObject("name", "haha"), 
	            new BasicDBObject("name", "dingding"),
	            true,//如果数据库不存在，是否添加
	            true//false只修改第一天，true如果有多条就不修改
	            ).getN());
	    
	    //当数据库不存在就不修改、不添加数据，当多条数据就不修改
	    //System.out.println("修改多条：" + coll.updateMulti(new BasicDBObject("_id", new ObjectId("4dde23616be7c19df07db42c")), new BasicDBObject("name", "199")));
	}
	
	public void query() {
	    //查询所有
	    //queryAll();
		
		Mongo mongo = getMongo();
		DB db = mongo.getDB("test");
		DBCollection users = db.getCollection("user");
	    
	    //查询id = 4de73f7acd812d61b4626a77
		System.out.println("find id = 4de73f7acd812d61b4626a77: " + users.find(new BasicDBObject("_id", new ObjectId("4de73f7acd812d61b4626a77"))).toArray());
	    
	    //查询age = 24
		System.out.println("find age = 24: " + users.find(new BasicDBObject("age", 24)).toArray());
	    
	    //查询age >= 24
		System.out.println("find age >= 24: " + users.find(new BasicDBObject("age", new BasicDBObject("$gte", 24))).toArray());
		System.out.println("find age <= 24: " + users.find(new BasicDBObject("age", new BasicDBObject("$lte", 24))).toArray());
	    
		System.out.println("查询age!=25：" + users.find(new BasicDBObject("age", new BasicDBObject("$ne", 25))).toArray());
		System.out.println("查询age in 25/26/27：" + users.find(new BasicDBObject("age", new BasicDBObject(QueryOperators.IN, new int[] { 25, 26, 27 }))).toArray());
		System.out.println("查询age not in 25/26/27：" + users.find(new BasicDBObject("age", new BasicDBObject(QueryOperators.NIN, new int[] { 25, 26, 27 }))).toArray());
		System.out.println("查询age exists 排序：" + users.find(new BasicDBObject("age", new BasicDBObject(QueryOperators.EXISTS, true))).toArray());
	    
		System.out.println("只查询age属性：" + users.find(null, new BasicDBObject("age", true)).toArray());
		System.out.println("只查属性：" + users.find(null, new BasicDBObject("age", true), 0, 2).toArray());
		System.out.println("只查属性：" + users.find(null, new BasicDBObject("age", true), 0, 2, Bytes.QUERYOPTION_NOTIMEOUT).toArray());
	    
	    //只查询一条数据，多条去第一条
		System.out.println("findOne: " + users.findOne());
		System.out.println("findOne: " + users.findOne(new BasicDBObject("age", 26)));
		System.out.println("findOne: " + users.findOne(new BasicDBObject("age", 26), new BasicDBObject("name", true)));
	    
	    //查询修改、删除
		System.out.println("findAndRemove 查询age=25的数据，并且删除: " + users.findAndRemove(new BasicDBObject("age", 25)));
	    
	    //查询age=26的数据，并且修改name的值为Abc
		System.out.println("findAndModify: " + users.findAndModify(new BasicDBObject("age", 26), new BasicDBObject("name", "Abc")));
		System.out.println("findAndModify: " + users.findAndModify(
	        new BasicDBObject("age", 28), //查询age=28的数据
	        new BasicDBObject("name", true), //查询name属性
	        new BasicDBObject("age", true), //按照age排序
	        false, //是否删除，true表示删除
	        new BasicDBObject("name", "Abc"), //修改的值，将name修改成Abc
	        true, 
	        true));
		//mongoDB不支持联合查询、子查询，这需要我们自己在程序中完成。将查询的结果集在Java查询中进行需要的过滤即可。
	    
	}
	
	public void testOthers() {
		
		Mongo mongo = getMongo();
		DB db = mongo.getDB("test");
		DBCollection users = db.getCollection("user");
		
	    DBObject user = new BasicDBObject();
	    user.put("name", "hoojo");
	    user.put("age", 24);
	    
	    //JSON 对象转换        
	    System.out.println("serialize: " + JSON.serialize(user));
	    //反序列化
	    System.out.println("parse: " + JSON.parse("{ \"name\" : \"hoojo\" , \"age\" : 24}"));
	    
	    System.out.println("判断temp Collection是否存在: " + db.collectionExists("temp"));
	    
	    //如果不存在就创建
	    if (!db.collectionExists("temp")) {
	        DBObject options = new BasicDBObject();
	        options.put("size", 20);
	        options.put("capped", 20);
	        options.put("max", 20);
	        System.out.println(db.createCollection("account", options));
	    }
	    
	    //设置db为只读
	    db.setReadOnly(true);
	    
	    //只读不能写入数据
	    db.getCollection("test").save(user);
	}
	
	public void queryAll() {
		Mongo mongo = getMongo();
		DB db = mongo.getDB("test");
		DBCollection users = db.getCollection("user");
		
		 System.out.println("查询users的所有数据：");
	    //db游标
	    DBCursor cur = users.find();
	    while (cur.hasNext()) {
	    	 System.out.println(cur.next());
	    }
	}
	
	
	
	public static void main(String[] args) {
		add();
	}

}
