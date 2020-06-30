package com.tsingxin.controller.viewobject;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class CourseVO {
    private Integer id;

    //课程名称
    private String title;

    //课程学分
    private Integer credit;

    //课程名额库存
    private Integer stock;

    //课程描述
    private String description;

    //考核方式
    private String mode;

    //开课院系
    private String faculty;

    //任课教师
    private String teacher;

    //上课地点
    private String place;

    //上课时间
    private String time;

    //记录课程是否在抢课活动中，以及对应的状态，0表示没有抢课活动，1抢课活动待开始，2抢课活动进行中
    private Integer grabStatus;

    //抢课活动id
    private Integer grabId;

    //抢课活动开始时间
    private String startDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getGrabStatus() {
        return grabStatus;
    }

    public void setGrabStatus(Integer grabStatus) {
        this.grabStatus = grabStatus;
    }

    public Integer getGrabId() {
        return grabId;
    }

    public void setGrabId(Integer grabId) {
        this.grabId = grabId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
