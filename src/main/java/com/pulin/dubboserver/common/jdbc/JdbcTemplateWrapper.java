package com.pulin.dubboserver.common.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.pulin.dubboserver.exception.DaoException;


public class JdbcTemplateWrapper<T,PK> {
	
	private static Logger logger = LoggerFactory.getLogger(JdbcTemplateWrapper.class);
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	
	private Class<T> clazz;
	
	/**
     * javabean中序列化版本UID属性名称
     */
    private static final String SVUID_NAME = "serialVersionUID";
    /**
     * 默认的主键名
     */
    private static final String DEFAULT_PRIMARY_KEY = "id";

	public JdbcTemplateWrapper() {
		ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
		this.clazz = (Class<T>) pt.getActualTypeArguments()[0];
	}

	public RowMapper getRowMapper() {
		return new BeanPropertyRowMapper(clazz);
	}
	
	 /**
     * 获得表名
     *
     * @return
     */
    protected String getTableName() {
        String tableName = null;
        if (this.getClass().isAnnotationPresent(TableName.class) && StringUtils.isNotBlank(this.getClass().getAnnotation(TableName.class).value())) {
            tableName = this.getClass().getAnnotation(TableName.class).value();
        } else {
            tableName = clazz.getSimpleName();
            tableName = tableName.length() > 1 ? (tableName.substring(0, 1) + tableName.substring(1).replaceAll("([A-Z])", "_$1")) : tableName;
            tableName = tableName.toLowerCase();
        }
        return tableName;
    }
    
    /**
     * 获得主键名
     *
     * @return
     */
    protected String getPrimarkKeyName() {
        String primaryKeyName = DEFAULT_PRIMARY_KEY;//主键名
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (f.getName().equals(SVUID_NAME))
                continue;
            if (f.isAnnotationPresent(PrimaryKey.class)) {
                primaryKeyName = f.getName();
                break;
            }
        }
        return primaryKeyName;
    }
    
    /**
     * 类的变量名转换成表的字段名
     *
     * @param fieldName
     * @return
     */
    protected String fieldName2ColumnName(String fieldName) {
        String result = fieldName;
        int mapping = FieldMapping.ORIGINAL;
        if (this.getClass().isAnnotationPresent(FieldMapping.class)) {
            mapping = this.getClass().getAnnotation(FieldMapping.class).value();
        }
        if (mapping == FieldMapping.CONVERT) {
            result = result.length() > 1 ? (result.substring(0, 1) + result.substring(1).replaceAll("([A-Z])", "_$1")) : result;
            result = "`" + result.toLowerCase() + "`";
        } else {
            result = "`" + fieldName + "`";
        }
        return result;
    }
	
	
	
	 /**
     * 获取对象中某个字段的值
     *
     * @param obj
     * @param f
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    protected Object getFieldValue(T obj, Field f) throws IllegalArgumentException, IllegalAccessException {
        f.setAccessible(true);
        return f.get(obj);
    }

    /**
     * 新增一条记录
     *
     * @param obj
     * @return
     * @throws DaoException
     */
    public int insert(T obj) throws DaoException {
        return insert(obj, false);
    }

    /**
     * 新增一条记录
     *
     * @param obj
     * @param ignore 如果此条记录已存在则忽略，并返回0(基于唯一索引)
     * @return
     * @throws DaoException
     */
    public int insert(T obj, boolean ignore) throws DaoException {
        int count = 0;
        try {
            if (obj != null) {
                String tableName = getTableName();
                StringBuilder sb = new StringBuilder();
                int size = 0;
                sb.append("insert " + (ignore ? "ignore " : "") + "into " + tableName + "(");
                Field[] fields = obj.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getName().equals(SVUID_NAME))
                        continue;
                    if (getFieldValue(obj, f) == null)
                        continue;
                    sb.append(fieldName2ColumnName(f.getName()) + ",");
                    size++;
                }
                sb = sb.length() > 0 && sb.charAt(sb.length() - 1) == ',' ? sb.deleteCharAt(sb.length() - 1) : sb;
                sb.append(") values(");
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getName().equals(SVUID_NAME))
                        continue;
                    if (getFieldValue(obj, f) == null)
                        continue;
                    sb.append("?,");
                }
                sb = sb.length() > 0 && sb.charAt(sb.length() - 1) == ',' ? sb.deleteCharAt(sb.length() - 1) : sb;
                sb.append(")");
                Object[] objs = new Object[size];
                int index = 0;
                for (Field f : fields) {
                    if (f.getName().equals(SVUID_NAME))
                        continue;
                    Object v = getFieldValue(obj, f);
                    if (v == null)
                        continue;
                    objs[index++] = v;
                }
                count = jdbcTemplate.update(sb.toString(), objs);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return count;
    }

    /**
     * 替换一条记录，如果该记录不存在则新增，如果已存在则删除后再新增(基于唯一索引)
     *
     * @param obj
     * @return
     * @throws DaoException
     */
    public int replace(T obj) throws DaoException {
        int count = 0;
        try {
            if (obj != null) {
                String tableName = getTableName();
                StringBuilder sb = new StringBuilder();
                int size = 0;
                sb.append("replace into " + tableName + "(");
                Field[] fields = obj.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getName().equals(SVUID_NAME))
                        continue;
                    if (getFieldValue(obj, f) == null)
                        continue;
                    sb.append(fieldName2ColumnName(f.getName()) + ",");
                    size++;
                }
                sb = sb.length() > 0 && sb.charAt(sb.length() - 1) == ',' ? sb.deleteCharAt(sb.length() - 1) : sb;
                sb.append(") values(");
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getName().equals(SVUID_NAME))
                        continue;
                    if (getFieldValue(obj, f) == null)
                        continue;
                    sb.append("?,");
                }
                sb = sb.length() > 0 && sb.charAt(sb.length() - 1) == ',' ? sb.deleteCharAt(sb.length() - 1) : sb;
                sb.append(")");
                Object[] objs = new Object[size];
                int index = 0;
                for (Field f : fields) {
                    if (f.getName().equals(SVUID_NAME))
                        continue;
                    Object v = getFieldValue(obj, f);
                    if (v == null)
                        continue;
                    objs[index++] = v;
                }
                count = jdbcTemplate.update(sb.toString(), objs);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return count;
    }

    /**
     * 新增多条记录
     *
     * @param list
     * @return
     * @throws DaoException
     */
    public int insertAll(List<T> list) throws DaoException {
        int count = 0;
        try {
            if (list != null && list.size() > 0) {
                List<Object[]> batchArgs = new ArrayList<Object[]>();
                String tableName = getTableName();
                StringBuilder sb = new StringBuilder();
                int size = 0;
                sb.append("insert into " + tableName + "(");
                Field[] fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getName().equals(SVUID_NAME))
                        continue;
                    sb.append(fieldName2ColumnName(f.getName()) + ",");
                    size++;
                }
                sb = sb.length() > 0 && sb.charAt(sb.length() - 1) == ',' ? sb.deleteCharAt(sb.length() - 1) : sb;
                sb.append(") values(");
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getName().equals(SVUID_NAME))
                        continue;
                    sb.append("?,");
                }
                sb = sb.length() > 0 && sb.charAt(sb.length() - 1) == ',' ? sb.deleteCharAt(sb.length() - 1) : sb;
                sb.append(")");
                for (int i = 0; i < list.size(); i++) {
                    Object[] objs = new Object[size];
                    int index = 0;
                    for (Field f : fields) {
                        if (f.getName().equals(SVUID_NAME))
                            continue;
                        f.setAccessible(true);
                        objs[index++] = f.get(list.get(i));
                    }
                    batchArgs.add(objs);
                }
                int[] ii = jdbcTemplate.batchUpdate(sb.toString(), batchArgs);
                for (int s : ii) {
                    if (s > 0)
                        count++;
                }
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return count;
    }

    /**
     * 修改一条记录
     *
     * @param obj
     * @return
     * @throws DaoException
     */
    public int update(T obj) throws DaoException {
        int count = 0;
        try {
            if (obj != null) {
                String tableName = getTableName();
                StringBuilder sb = new StringBuilder();
                int size = 0;
                sb.append("update " + tableName + " set ");
                Field[] fields = obj.getClass().getDeclaredFields();
                String primaryKeyName = getPrimarkKeyName();//主键名
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getName().equals(SVUID_NAME))
                        continue;
                    if (!f.getName().equals(primaryKeyName)) {
                        sb.append(fieldName2ColumnName(f.getName()) + "=?,");
                    }
                    size++;
                }
                sb = sb.length() > 0 && sb.charAt(sb.length() - 1) == ',' ? sb.deleteCharAt(sb.length() - 1) : sb;
                sb.append(" where " + fieldName2ColumnName(primaryKeyName) + "=?");
                Object[] objs = new Object[size];
                int index = 0;
                Object primaryKeyValue = null;
                for (Field f : fields) {
                    if (f.getName().equals(SVUID_NAME))
                        continue;
                    f.setAccessible(true);
                    if (!f.getName().equals(primaryKeyName)) {
                        objs[index++] = f.get(obj);
                    } else {
                        primaryKeyValue = f.get(obj);
                    }
                }
                objs[index++] = primaryKeyValue;
                count = jdbcTemplate.update(sb.toString(), objs);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return count;
    }

    public int[] batchUpdate(String sql, List<Object[]> batchArgs) throws DaoException {
        try {
            return jdbcTemplate.batchUpdate(sql, batchArgs);
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    /**
     * 执行SQL命令，返回影响行数
     *
     * @param sql
     * @param args
     * @return
     * @throws DaoException
     */
    public int executeCommand(String sql, Object... args) throws DaoException {
        int count = 0;
        try {
            if (StringUtils.isNotBlank(sql)) {
                count = jdbcTemplate.update(sql, args);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return count;
    }

    /**
     * 根据PK获得一条记录
     *
     * @param pk
     * @return
     * @throws DaoException
     */
    public T get(PK pk) throws DaoException {
        T result = null;
        try {
            String tableName = getTableName();
            String primaryKeyName = getPrimarkKeyName();
            String sql = "select * from " + tableName + " where " + primaryKeyName + "=?";
            result = (T) DataAccessUtils.singleResult(jdbcTemplate.query(sql, new Object[]{pk},getRowMapper()));
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }

    /**
     * 根据条件获得一条记录
     *
     * @param where
     * @return
     * @throws DaoException
     */
    public T getOneByWhere(String where, Object... args) throws DaoException {
        T result = null;
        try {
            if (StringUtils.isNotBlank(where)) {
                String tableName = getTableName();
                String sql = "select * from " + tableName + " where " + where;
                List<T> list = getListByWhere(where,args);
                if(list != null && list.size() > 0 ){
                    return  list.get(0);
                }
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }

    /**
     * 根据sql获得一条记录
     *
     * @param sql
     * @return
     * @throws DaoException
     */
    public T getOneBySql(String sql, Object... args) throws DaoException {
        T result = null;
        try {
            if (StringUtils.isNotBlank(sql)) {
                List<T> list = getListBySql(sql, args);
                if(list != null && list.size() > 0 ){
                    return  list.get(0);
                }
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }

    /**
     * 根据id列表查询记录列表
     *
     * @param idList
     * @return
     * @throws DaoException
     */
    public List<T> getListByIdList(List<Long> idList) throws DaoException {
        List<T> result = null;
        try {
            if (idList != null && idList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < idList.size(); i++) {
                    Long id = idList.get(i);
                    if (id == null)
                        continue;
                    sb.append(id + ",");
                }
                if (sb.toString().trim().length() > 0) {
                    sb = sb.length() > 0 && sb.charAt(sb.length() - 1) == ',' ? sb.deleteCharAt(sb.length() - 1) : sb;
                    String tableName = getTableName();
                    String primaryKeyName = getPrimarkKeyName();
                    String sql = "select * from " + tableName + " where " + primaryKeyName + " in(" + sb.toString() + ")";
                    result = jdbcTemplate.query(sql, getRowMapper());
                }
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }

    /**
     * 根据id列表查询记录MAP
     *
     * @param idList
     * @return
     * @throws DaoException
     */
    public Map<Long, T> getMapByIdList(List<Long> idList) throws DaoException {
        Map<Long, T> map = new LinkedHashMap<Long, T>();
        try {
            List<T> list = getListByIdList(idList);
            if (list != null && list.size() > 0) {
                Field id = null;
                String primaryKeyName = getPrimarkKeyName();
                Field[] fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getName().equals(primaryKeyName)) {
                        f.setAccessible(true);
                        id = f;
                    }
                }
                for (T t : list) {
                    map.put(Long.parseLong(id.get(t).toString()), t);
                }
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return map;
    }

    /**
     * 根据字符型id列表查询记录列表
     *
     * @param idList
     * @return
     * @throws DaoException
     */
    public List<T> getListByIdStrList(List<String> idList) throws DaoException {
        List<T> result = null;
        try {
            if (idList != null && idList.size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < idList.size(); i++) {
                    String id = idList.get(i);
                    if (StringUtils.isBlank(id))
                        continue;
                    sb.append("'" + id + "',");
                }
                if (sb.toString().trim().length() > 0) {
                    sb = sb.length() > 0 && sb.charAt(sb.length() - 1) == ',' ? sb.deleteCharAt(sb.length() - 1) : sb;
                    String tableName = getTableName();
                    String primaryKeyName = getPrimarkKeyName();
                    String sql = "select * from " + tableName + " where " + primaryKeyName + " in(" + sb.toString() + ")";
                    result = jdbcTemplate.query(sql, getRowMapper());
                }
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }


    /**
     * 根据id列表查询记录MAP
     *
     * @param idList
     * @return
     * @throws DaoException
     */
    public Map<String, T> getMapByIdStrList(List<String> idList) throws DaoException {
        Map<String, T> map = new LinkedHashMap<String, T>();
        try {
            List<T> list = getListByIdStrList(idList);
            if (list != null && list.size() > 0) {
                Field id = null;
                String primaryKeyName = getPrimarkKeyName();
                Field[] fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getName().equals(primaryKeyName)) {
                        f.setAccessible(true);
                        id = f;
                    }
                }
                for (T t : list) {
                    map.put(id.get(t).toString(), t);
                }
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return map;
    }


    /**
     * 根据查询条件查询记录列表
     *
     * @param where
     * @return
     * @throws DaoException
     */
    public List<T> getListByWhere(String where, Object... args) throws DaoException {
        List<T> result = null;
        try {
            if (StringUtils.isNotBlank(where)) {
                String tableName = getTableName();
                String sql = "select * from " + tableName + " where " + where;
                result = jdbcTemplate.query(sql, args, getRowMapper());
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }

    /**
     * 统计
     *
     * @param where
     * @param args
     * @return
     * @throws DaoException
     */
    public Integer countByWhere(String where, Object... args) throws DaoException {
        Integer result = null;
        try {
            if (StringUtils.isNotBlank(where)) {
                String tableName = getTableName();
                String sql = "select count(*) from " + tableName + " where " + where;
                result = getCountBySql(sql,args);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }


    /**
     * 根据sql查询记录列表
     *
     * @param sql
     * @return
     * @throws DaoException
     */
    public List<T> getListBySql(String sql, Object... args) throws DaoException {
        List<T> result = null;
        try {
            if (StringUtils.isNotBlank(sql)) {
                result = jdbcTemplate.query(sql, args, getRowMapper());
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }

    /**
     * 根据查询条件查询记录MAP
     *
     * @param where
     * @return
     * @throws DaoException
     */
    public Map<Long, T> getMapByWhere(String where, Object... args) throws DaoException {
        Map<Long, T> map = new LinkedHashMap<Long, T>();
        try {
            List<T> list = getListByWhere(where, args);
            if (list != null && list.size() > 0) {
                Field id = null;
                String primaryKeyName = getPrimarkKeyName();
                Field[] fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getName().equals(primaryKeyName)) {
                        f.setAccessible(true);
                        id = f;
                    }
                }
                for (T t : list) {
                    map.put(Long.parseLong(id.get(t).toString()), t);
                }
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return map;
    }

    /**
     * 根据查询条件查询记录MAP，指定map中的key，key必须是javabean中存在的属性，且对应表中的字段是唯一的
     *
     * @param where 查询条件
     * @param key   javabean中对应的属性名称
     * @return
     * @throws DaoException
     */
    public Map<String, T> getMapByWhereForKey(String where, String key) throws DaoException {
        Map<String, T> map = new LinkedHashMap<String, T>();
        try {
            List<T> list = getListByWhere(where);
            if (list != null && list.size() > 0) {
                Field id = null;
                String primaryKeyName = StringUtils.isNotBlank(key) ? key : getPrimarkKeyName();
                Field[] fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getName().equals(primaryKeyName)) {
                        f.setAccessible(true);
                        id = f;
                    }
                }
                for (T t : list) {
                    if (id != null)
                        map.put(id.get(t).toString(), t);
                }
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return map;
    }

    /**
     * 获得记录总数
     *
     * @return
     * @throws DaoException
     */
    public int getCount() throws DaoException {
        int result = 0;
        try {
            String tableName = getTableName();
            String sql = "select count(1) from " + tableName;
            result = jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }

    /**
     * 根据查询条件查询记录总数
     *
     * @param where
     * @return
     * @throws DaoException
     */
    public int getCountByWhere(String where, Object... args) throws DaoException {
        int result = 0;
        try {
            if (StringUtils.isNotBlank(where)) {
                String tableName = getTableName();
                String sql = "select count(1) from " + tableName + " where " + where;
                result = jdbcTemplate.queryForObject(sql, args, Integer.class);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }

    /**
     * sql 统计
     *
     * @param sql
     * @param args
     * @return
     * @throws DaoException
     */
    public int getCountBySql(String sql, Object... args) throws DaoException {
        int result = 0;
        try {
            result = jdbcTemplate.queryForObject(sql, args, Integer.class);
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }

    /**
     * 获得所有记录列表
     *
     * @return
     * @throws DaoException
     */
    public List<T> getAllList() throws DaoException {
        List<T> result = null;
        try {
            String tableName = getTableName();
            String primaryKeyName = getPrimarkKeyName();
            String sql = "select * from " + tableName + " order by " + primaryKeyName;
            result = jdbcTemplate.query(sql, getRowMapper());
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }

    /**
     * 根据SQL查询MAP列表
     *
     * @return
     * @throws DaoException
     */
    public List<Map<String, Object>> getMapList(String sql, Object... args) throws DaoException {
        List<Map<String, Object>> result = null;
        try {
            result = jdbcTemplate.queryForList(sql, args);
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }

    /**
     * 获取单个map
     *
     * @param sql
     * @param args
     * @return
     * @throws DaoException
     */
    public Map<String, Object> getSingleMap(String sql, Object... args) throws DaoException {
        Map<String, Object> result = null;
        try {
            List<Map<String, Object>> ls = getMapList(sql, args);
            if (ls != null && ls.size() > 0) {
                result = ls.get(0);
            }
        } catch (Exception e) {
            throw new DaoException(e);
        }
        return result;
    }



    /**
     * 针对单表简单SQL查询返回单一对象
     *
     * @param paramMap 查询条件
     * @return
     * @throws DaoException
     * @author tany@shishike.com
     * @2015年1月22日
     */
    public T getOneByWhereCase(Map<String, Object> paramMap) throws DaoException {
        Iterator<Entry<String, Object>> keys = paramMap.entrySet().iterator();
        List<Object> paramList = new ArrayList<Object>();
        StringBuffer whereCase = new StringBuffer(" 1=1 AND ");
        while (keys.hasNext()) {
            Entry<String, Object> entry = keys.next();
            whereCase.append(entry.getKey()).append("=? AND ");
            paramList.add(entry.getValue());
        }
        int length = whereCase.length();
        whereCase = length > 0 && whereCase.lastIndexOf("AND ") > 0 ? whereCase.delete(length - 4, length) : whereCase;
        return getOneByWhere(whereCase.toString(), paramList.toArray());
    }


    /**
     * 针对单表简单SQL查询返回List结果集
     *
     * @param paramMap
     * @param orderBy
     * @return
     * @throws DaoException
     * @author tany@shishike.com
     * @2015年1月22日
     */
    public List<T> getListByWhereCase(Map<String, Object> paramMap, String orderBy) throws DaoException {
        Iterator<Entry<String, Object>> keys = paramMap.entrySet().iterator();
        List<Object> paramList = new ArrayList<>();
        StringBuffer whereCase = new StringBuffer(" 1=1 AND ");
        while (keys.hasNext()) {
            Entry<String, Object> entry = keys.next();
            whereCase.append(entry.getKey()).append("=? AND ");
            paramList.add(entry.getValue());
        }
        int length = whereCase.length();
        whereCase = length > 0 && whereCase.lastIndexOf("AND ") > 0 ? whereCase.delete(length - 4, length) : whereCase;
        whereCase.append(orderBy);
        return getListByWhere(whereCase.toString(), paramList.toArray());
    }

    /**
     * 针对单表简单SQL统计条件查询的数据
     *
     * @param paramMap
     * @return
     * @throws DaoException
     * @author tany@shishike.com
     * @2015年1月22日
     */
    public int countByWhereCase(Map<String, Object> paramMap) throws DaoException {
        Iterator<Entry<String, Object>> keys = paramMap.entrySet().iterator();
        List<Object> paramList = new ArrayList<>();
        StringBuffer whereCase = new StringBuffer(" 1=1 AND ");
        while (keys.hasNext()) {
            Entry<String, Object> entry = keys.next();
            whereCase.append(entry.getKey()).append("=? AND ");
            paramList.add(entry.getValue());
        }
        int length = whereCase.length();
        whereCase = length > 0 && whereCase.lastIndexOf("AND ") > 0 ? whereCase.delete(length - 4, length) : whereCase;
        return getCountByWhere(whereCase.toString(), paramList.toArray());
    }

    /**
     * 根据SQL查询单个值
     *
     * @return
     * @throws DaoException
     */
    public <E> E getSingleValue(String sql, Class<E> clazz, Object... args) throws DaoException {
        E result = null;
        try {
            result = jdbcTemplate.queryForObject(sql, clazz, args);
        } catch (Exception e) {
            if (!(e instanceof EmptyResultDataAccessException))
                throw new DaoException(e);
        }
        return result;
    }
}
