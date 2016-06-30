package com.pulin.dubboserver.common.jdbc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段映射，作用在DAO上
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMapping {
	public int value();
	/**
	 * javabean字段与表字段相同
	 */
	public final static int ORIGINAL=1;
	/**
	 * 将javabean字段的大写转成小写，并在前面加下划线
	 */
	public final static int CONVERT=2;
}
