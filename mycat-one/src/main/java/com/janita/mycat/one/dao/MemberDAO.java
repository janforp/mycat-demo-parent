package com.janita.mycat.one.dao;

import com.janita.mycat.one.bean.Member;

import java.util.List;

/**
 * Created by com.janita.mycat.one.MybatisCodeGenerate on 2017-03-15
 */
public interface MemberDAO {
    int deleteByPrimaryKey(String id);

    void insert(Member record);

    void insertSelective(Member record);

    void insertBatch(List<Member> records);

    Member selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Member record);

    int updateByPrimaryKey(Member record);
}