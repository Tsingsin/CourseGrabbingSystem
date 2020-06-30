package com.tsingxin.service.model;

import java.math.BigDecimal;

//用户下单的交易模型
public class OrderModel {

    //2018102100012828 交易号
    private String id;

    //用户id
    private Integer userId;

    //若非空，则表示以抢课方式下单
    private Integer grabId;

    //课程id
    private Integer courseId;

    //数量
    private Integer amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getGrabId() {
        return grabId;
    }

    public void setGrabId(Integer grabId) {
        this.grabId = grabId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
