package com.janita.mycat.two.bean;

/**
 * Created by com.janita.mycat.two.MybatisCodeGenerate on 2017-03-15
 */
public class Food implements java.io.Serializable {

    // Fields

    // id
    private Long foodId;
    // 食物名字
    private String foodName;
    // 备注
    private String foodRemark;

    // Constructors

    /**
     * default constructor
     */
    public Food() {
    }

    /**
     * full constructor
     */
    public Food(Long foodId, String foodName, String foodRemark) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodRemark = foodRemark;
    }

    // Property accessors

    /**
     * id
     */
    public Long getFoodId() {
        return this.foodId;
    }

    /**
     * id
     */
    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    /**
     * 食物名字
     */
    public String getFoodName() {
        return this.foodName;
    }

    /**
     * 食物名字
     */
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    /**
     * 备注
     */
    public String getFoodRemark() {
        return this.foodRemark;
    }

    /**
     * 备注
     */
    public void setFoodRemark(String foodRemark) {
        this.foodRemark = foodRemark;
    }

}