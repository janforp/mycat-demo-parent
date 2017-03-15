package com.janita.mycat.one.bean;

/**
 * Created by com.janita.mycat.one.MybatisCodeGenerate on 2017-03-15
 */
public class User implements java.io.Serializable {

    // Fields

    // id
    private String id;
    // 名字
    private String userName;
    // 年龄
    private Integer age;

    // Constructors

    /**
     * default constructor
     */
    public User() {
    }

    /**
     * full constructor
     */
    public User(String id, String userName, Integer age) {
        this.id = id;
        this.userName = userName;
        this.age = age;
    }

    // Property accessors

    /**
     * id
     */
    public String getId() {
        return this.id;
    }

    /**
     * id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 名字
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * 名字
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 年龄
     */
    public Integer getAge() {
        return this.age;
    }

    /**
     * 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

}