package com.pulin.dubboserver.utils;

import java.io.Serializable;

/**
 * Created by pulin on 2016/7/4 0001.
 */
public class OrderErrorRequest implements Serializable{

	  private String  source;//订单来源

	    private Long startTime;//  
	    private Long endTime;//  

	    private String shopId;//商户ID

	    private Integer  condition;//1 订单号  2错误日志内容

	    private String content;//内容

	    private Integer pageNo = 1;//默认第一页

	    private Integer pageSize = 20;////默认煤每页20条数据

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public Long getStartTime() {
			return startTime;
		}

		public void setStartTime(Long startTime) {
			this.startTime = startTime;
		}

		public Long getEndTime() {
			return endTime;
		}

		public void setEndTime(Long endTime) {
			this.endTime = endTime;
		}

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

		public Integer getCondition() {
			return condition;
		}

		public void setCondition(Integer condition) {
			this.condition = condition;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public Integer getPageNo() {
			return pageNo;
		}

		public void setPageNo(Integer pageNo) {
			this.pageNo = pageNo;
		}

		public Integer getPageSize() {
			return pageSize;
		}

		public void setPageSize(Integer pageSize) {
			this.pageSize = pageSize;
		}

	    
}
