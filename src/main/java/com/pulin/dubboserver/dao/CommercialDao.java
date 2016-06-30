package com.pulin.dubboserver.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pulin.dubboserver.common.jdbc.JdbcTemplateWrapper;
import com.pulin.dubboserver.exception.DaoException;
import com.pulin.dubboserver.to.Commercial;



@Repository
public class CommercialDao extends JdbcTemplateWrapper<Commercial,Long>{


   

    
    public List<Commercial> queryAll() throws DaoException {
        String sql = "select * from commercial order by commercialID desc limit 100";
        List<Commercial> list = jdbcTemplate.query(sql,getRowMapper());
        return list;
     }
    
    public List<Commercial> queryAll(int pageNo,int pageSize) throws DaoException {
    	Integer start = 0;
		if(pageNo <= 1){
			start = 0;
		}else{
			start = (pageNo-1) * pageSize;
		}
        String sql = "select * from commercial LIMIT ?,?";
        List<Commercial> list = jdbcTemplate.query(sql,new Object[]{start,pageSize},getRowMapper());
        return list;
     }


    

}
