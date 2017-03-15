package com.janita.mycat.two.dao;

import com.janita.mycat.two.bean.Orders;

import java.util.List;

/**
 * Created by com.janita.mycat.two.MybatisCodeGenerate on 2017-03-15
 */
public interface OrdersDAO {
    int deleteByPrimaryKey(String orderId);

    void insert(Orders record);

    void insertSelective(Orders record);

    void insertBatch(List<Orders> records);

    Orders selectByPrimaryKey(String orderId);

    int updateByPrimaryKeySelective(Orders record);

    int updateByPrimaryKey(Orders record);
}