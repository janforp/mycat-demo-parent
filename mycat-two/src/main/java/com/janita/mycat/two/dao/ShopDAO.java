package com.janita.mycat.two.dao;

import com.janita.mycat.two.bean.Shop;

import java.util.List;

/**
 * Created by com.janita.mycat.two.MybatisCodeGenerate on 2017-03-15
 */
public interface ShopDAO {
    int deleteByPrimaryKey(String id);

    void insert(Shop record);

    void insertSelective(Shop record);

    void insertBatch(List<Shop> records);

    Shop selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Shop record);

    int updateByPrimaryKey(Shop record);
}