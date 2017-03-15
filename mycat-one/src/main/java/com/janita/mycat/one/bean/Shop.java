package com.janita.mycat.one.bean;

/**
 * Created by com.janita.mycat.one.MybatisCodeGenerate on 2017-03-15
 */
public class Shop implements java.io.Serializable {

    // Fields

    // id
    private String id;
    // 名字
    private String userName;
    // 地址
    private String address;

    // Constructors

    /**
     * default constructor
     */
    public Shop() {
    }

    /**
     * full constructor
     */
    public Shop(String id, String userName, String address) {
        this.id = id;
        this.userName = userName;
        this.address = address;
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
     * 地址
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

}