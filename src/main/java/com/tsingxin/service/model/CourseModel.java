package com.tsingxin.service.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

public class CourseModel implements Serializable {

    private Integer id;

    //课程名称
    @NotBlank(message = "课程名称不能为空")
    private String title;

    //课程库存
    @NotNull(message = "库存不能不填")
    private Integer stock;

    //课程描述
    @NotBlank(message = "课程描述信息不能为空")
    private String description;

    //课程学分
    @NotNull(message = "课程学分不能为空")
    private Integer credit;

    //课程考核方式
    @NotBlank(message = "课程考核方式不能为空")
    private String mode;

    //开课院系
    @NotBlank(message = "开课院系不能为空")
    private String faculty;

    //任课教师
    @NotBlank(message = "任课教师不能为空")
    private String teacher;

    //上课地点
    @NotBlank(message = "上课地点不能为空")
    private String place;

    //上课时间
    @NotBlank(message = "上课时间不能为空")
    private String time;

    //使用聚合模型(java类嵌套)，如果grabModel不为空，则表示其拥有还未结束的秒杀活动（还未开始的和正在进行的）
    private GrabModel grabModel;

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

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
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

    public GrabModel getGrabModel() {
        return grabModel;
    }

    public void setGrabModel(GrabModel grabModel) {
        this.grabModel = grabModel;
    }
}
