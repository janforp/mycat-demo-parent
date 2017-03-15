package com.janita.mycat.two.dao;

import com.janita.mycat.two.bean.Food;

import java.util.List;

/**
 * Created by com.janita.mycat.two.MybatisCodeGenerate on 2017-03-15
 */
public interface FoodDAO {
    int deleteByPrimaryKey(Long foodId);

    void insert(Food record);

    void insertSelective(Food record);

    void insertBatch(List<Food> records);

    Food selectByPrimaryKey(Long foodId);

    int updateByPrimaryKeySelective(Food record);

    int updateByPrimaryKey(Food record);
}