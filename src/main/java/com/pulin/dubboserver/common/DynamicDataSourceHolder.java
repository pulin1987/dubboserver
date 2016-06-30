package com.pulin.dubboserver.common;

public class DynamicDataSourceHolder {
	  /**
    *
    */
	private static final ThreadLocal<String> holder = new ThreadLocal<String>();

   /**
    * 设置主数据
    */
   public static void setMasterDataSource() {
       holder.set(DataSourceType.MASTER.name().toLowerCase());
   }

   /**
    * 设置从数据源
    */
   public static void setSlaveDataSource() {
   	holder.set(DataSourceType.SLAVE.name().toLowerCase());
   }

   /**
    * 从线程池选择当前数据源
    * @return
    */
   public static String getDataSouce() {
       return holder.get();
   }
}
