package com.janita.mycat.one.bean;

/**
 * Created by com.janita.mycat.one.MybatisCodeGenerate on 2017-03-15
 */
public class Member implements java.io.Serializable {

    // Fields

    // id
    private String id;
    // 名字
    private String memberName;
    // 备注
    private String memberRemark;

    // Constructors

    /**
     * default constructor
     */
    public Member() {
    }

    /**
     * full constructor
     */
    public Member(String id, String memberName, String memberRemark) {
        this.id = id;
        this.memberName = memberName;
        this.memberRemark = memberRemark;
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
    public String getMemberName() {
        return this.memberName;
    }

    /**
     * 名字
     */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    /**
     * 备注
     */
    public String getMemberRemark() {
        return this.memberRemark;
    }

    /**
     * 备注
     */
    public void setMemberRemark(String memberRemark) {
        this.memberRemark = memberRemark;
    }

}