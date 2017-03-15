package com.janita.mycat.two.dao;

import com.janita.mycat.two.bean.OrderDetail;

import java.util.List;

/**
 * Created by com.janita.mycat.two.MybatisCodeGenerate on 2017-03-15
 */
public interface OrderDetailDAO {
    int deleteByPrimaryKey(String detailId);

    void insert(OrderDetail record);

    void insertSelective(OrderDetail record);

    void insertBatch(List<OrderDetail> records);

    OrderDetail selectByPrimaryKey(String detailId);

    int updateByPrimaryKeySelective(OrderDetail record);

    int updateByPrimaryKey(OrderDetail record);
}