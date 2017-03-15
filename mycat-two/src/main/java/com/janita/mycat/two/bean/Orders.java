package com.janita.mycat.two.bean;

/**
 * Created by com.janita.mycat.two.MybatisCodeGenerate on 2017-03-15
 */
public class Orders implements java.io.Serializable {

    // Fields

    // 订单id
    private String orderId;
    // 订单详情id
    private String detailId;
    // 订单类型
    private Integer orderType;
    // 备注
    private String remark;

    // Constructors

    /**
     * default constructor
     */
    public Orders() {
    }

    /**
     * full constructor
     */
    public Orders(String orderId, String detailId, Integer orderType, String remark) {
        this.orderId = orderId;
        this.detailId = detailId;
        this.orderType = orderType;
        this.remark = remark;
    }

    // Property accessors

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
     * 订单类型
     */
    public Integer getOrderType() {
        return this.orderType;
    }

    /**
     * 订单类型
     */
    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
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