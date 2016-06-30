package com.pulin.dubboserver.dao;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.alibaba.dubbo.container.Main;
import com.pulin.dubboserver.to.MongoDBOrderAutoCompleteTO;



@Repository
public class MongoDBOrderAutoCompleteDao {

    final static Logger logger = LoggerFactory.getLogger(MongoDBOrderAutoCompleteDao.class);

    @Autowired(required=false)
    private MongoTemplate mongoTemplate;



    public void save(MongoDBOrderAutoCompleteTO to) {
        try{
            mongoTemplate.save(to);
        }catch(Exception e){
            logger.error("mongodb save error,e:{}",e);
        }

    }


    public MongoDBOrderAutoCompleteTO update(MongoDBOrderAutoCompleteTO to) {
        try{
            Criteria criteria = Criteria.where("tradeId").is(to.getTradeId());
            Query query = new Query(criteria);
            Update update = new Update();
            buildUpdate(to, update);
            return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(false).upsert(true), MongoDBOrderAutoCompleteTO.class);
        }catch(Exception e){
            logger.error("mongodb update,e:{}",e);
            return null;
        }
    }


    public List<MongoDBOrderAutoCompleteTO> findList(Integer status) {
        try{
        	long s = System.currentTimeMillis();
            Criteria criteria = Criteria.where("expireTime").lte(new Date());
            criteria.and("status").is(status);
            Query query = new Query(criteria);
            List<MongoDBOrderAutoCompleteTO> list = mongoTemplate.find(query,MongoDBOrderAutoCompleteTO.class);
            long e = System.currentTimeMillis();
            System.out.println( (e-s) );
            return  list;
        }catch(Exception e){
            return null;
        }
    }
    

    public List<MongoDBOrderAutoCompleteTO> findList2(Integer status) {
        try{
        	long s = System.currentTimeMillis();
            Criteria criteria = Criteria.where("expireTime").gte(new Date());
            criteria.and("status").is(status);
            Query query = new Query(criteria);
            List<MongoDBOrderAutoCompleteTO> list = mongoTemplate.find(query,MongoDBOrderAutoCompleteTO.class);
            long e = System.currentTimeMillis();
            System.out.println( (e-s) );
            return  list;
        }catch(Exception e){
            return null;
        }
    }
    
    public List<MongoDBOrderAutoCompleteTO> findList2(Integer status,int pageNo,int pageSize) {
        try{
        	if(pageNo > 0){
        		pageNo=pageNo-1;
        	}else{
        		pageNo = 0;
        	}
        	long s = System.currentTimeMillis();
            Criteria criteria = Criteria.where("expireTime").gte(new Date());
            criteria.and("status").is(status);
            Query query = new Query(criteria).skip(pageNo*pageSize).limit(pageSize);
            List<MongoDBOrderAutoCompleteTO> list = mongoTemplate.find(query,MongoDBOrderAutoCompleteTO.class);
            long e = System.currentTimeMillis();
            System.out.println( (e-s) );
            return  list;
        }catch(Exception e){
            return null;
        }
    }
    

    public Long count(Integer status) {
        try{
        	long s = System.currentTimeMillis();
            Criteria criteria = Criteria.where("expireTime").gte(new Date());
            criteria.and("status").is(status);
            Query query = new Query(criteria);
            Long num = mongoTemplate.count(query, MongoDBOrderAutoCompleteTO.class);
            long e = System.currentTimeMillis();
            System.out.println( (e-s) );
            return  num;
        }catch(Exception e){
            return 0L;
        }
    }



    private void buildUpdate(Object obj, Update update) {
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
                        update.set(name, value);
                    }
                    f.setAccessible(false);
                }
            } catch (Exception e) {
                logger.error("mongodb update data buildUpdate :{}", e);
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz =
                // clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }//for end
    }
    
    public static void main(String[] args) {
		System.out.println(System.currentTimeMillis()-1000*60*60*2);
	}
    
    
  



}
