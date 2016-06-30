package com.pulin.dubboserver.to;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pulin on 2016/6/8 0008.
 */
@Document(collection="orderAutoComplete")
public class MongoDBOrderAutoCompleteTO {
    @Id
    private String _id;
    private Long shopId;//商户ID
    @Indexed
    private Long tradeId;//订单ID
    private String orderId;//第三方订单ID
    private Integer source;//订单来源
    private Integer status;//状态  0 未自動完成   1 已自動完成
    private Date expireTime;//过期时间 自动完成时间 到此时间点自动完成
    private Date createTime;
    private Date modifyTime;
    private Long dateNum;//20160623 这样的数字

    public MongoDBOrderAutoCompleteTO(){
        this.dateNum = Long.parseLong(sdf.format(new Date()));
    }


    private static DateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Long getDateNum() {
        return dateNum;
    }

    public void setDateNum(Long dateNum) {
        this.dateNum = dateNum;
    }

	@Override
	public String toString() {
		return "MongoDBOrderAutoCompleteTO [_id=" + _id + ", shopId=" + shopId + ", tradeId=" + tradeId + ", orderId="
				+ orderId + ", source=" + source + ", status=" + status + ", expireTime=" + expireTime + ", createTime="
				+ createTime + ", modifyTime=" + modifyTime + ", dateNum=" + dateNum + "]";
	}

    
    
    
}
