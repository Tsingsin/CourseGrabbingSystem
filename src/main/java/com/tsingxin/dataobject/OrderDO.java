package com.tsingxin.dataobject;

public class OrderDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    private String id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.user_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    private Integer userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.course_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    private Integer courseId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.amount
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    private Integer amount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.grab_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    private Integer grabId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.id
     *
     * @return the value of order_info.id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.id
     *
     * @param id the value for order_info.id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.user_id
     *
     * @return the value of order_info.user_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.user_id
     *
     * @param userId the value for order_info.user_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.course_id
     *
     * @return the value of order_info.course_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public Integer getCourseId() {
        return courseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.course_id
     *
     * @param courseId the value for order_info.course_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.amount
     *
     * @return the value of order_info.amount
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public Integer getAmount() {
        return amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.amount
     *
     * @param amount the value for order_info.amount
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.grab_id
     *
     * @return the value of order_info.grab_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public Integer getGrabId() {
        return grabId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.grab_id
     *
     * @param grabId the value for order_info.grab_id
     *
     * @mbg.generated Thu Jun 11 15:49:38 CST 2020
     */
    public void setGrabId(Integer grabId) {
        this.grabId = grabId;
    }
}