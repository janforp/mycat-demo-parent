package com.janita.mycat.two.dao;

import com.janita.mycat.two.bean.User;

import java.util.List;

/**
 * Created by com.janita.mycat.two.MybatisCodeGenerate on 2017-03-15
 */
public interface UserDAO {
    int deleteByPrimaryKey(String id);

    void insert(User record);

    void insertSelective(User record);

    void insertBatch(List<User> records);

    User selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}