package com.janita.mycat.two.bean;

/**
 * Created by com.janita.mycat.two.MybatisCodeGenerate on 2017-03-16
 */
public class OrderDetail implements java.io.Serializable {

    // Fields

    // 订单详情id
    private String detailId;
    // 订单id
    private String orderId;
    // 订单详情
    private String detailContent;
    // 备注
    private String remark;

    // Constructors

    /**
     * default constructor
     */
    public OrderDetail() {
    }

    /**
     * full constructor
     */
    public OrderDetail(String detailId, String orderId, String detailContent, String remark) {
        this.detailId = detailId;
        this.orderId = orderId;
        this.detailContent = detailContent;
        this.remark = remark;
    }

    // Property accessors

    /**
     * 订单详情id
     */
    public String getDetailId() {
        return this.detailId;
    }

    /**
     * 订单详情id
     */
    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    /**
     * 订单id
     */
    public String getOrderId() {
        return this.orderId;
    }

    /**
     * 订单id
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * 订单详情
     */
    public String getDetailContent() {
        return this.detailContent;
    }

    /**
     * 订单详情
     */
    public void setDetailContent(String detailContent) {
        this.detailContent = detailContent;
    }

    /**
     * 备注
     */
    public String getRemark() {
        return this.remark;
    }

    /**
     * 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}