package com.tsingxin.dataobject;

import java.util.Date;

public class GrabDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column grab.id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column grab.grab_name
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    private String grabName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column grab.start_date
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    private Date startDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column grab.course_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    private Integer courseId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column grab.end_date
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    private Date endDate;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column grab.id
     *
     * @return the value of grab.id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column grab.id
     *
     * @param id the value for grab.id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column grab.grab_name
     *
     * @return the value of grab.grab_name
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public String getGrabName() {
        return grabName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column grab.grab_name
     *
     * @param grabName the value for grab.grab_name
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public void setGrabName(String grabName) {
        this.grabName = grabName == null ? null : grabName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column grab.start_date
     *
     * @return the value of grab.start_date
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column grab.start_date
     *
     * @param startDate the value for grab.start_date
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column grab.course_id
     *
     * @return the value of grab.course_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public Integer getCourseId() {
        return courseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column grab.course_id
     *
     * @param courseId the value for grab.course_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column grab.end_date
     *
     * @return the value of grab.end_date
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column grab.end_date
     *
     * @param endDate the value for grab.end_date
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}