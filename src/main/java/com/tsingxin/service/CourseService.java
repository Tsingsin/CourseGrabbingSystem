package com.tsingxin.service;

import com.tsingxin.error.BusinessException;
import com.tsingxin.service.model.CourseModel;

import java.util.List;

public interface CourseService {

    //创建课程
    CourseModel creatCourse(CourseModel courseModel) throws BusinessException;

    //课程列表浏览
    List<CourseModel> listCourse();

    //课程详情浏览
    CourseModel getCourseById(Integer id);

    //course及grab model缓存模型，验证course及grab是否有效
    CourseModel getCourseByIdInCache(Integer id);

    //库存扣减
    boolean decreaseStock(Integer courseId, Integer amount) throws BusinessException;

    //库存回补
    boolean increaseStock(Integer courseId, Integer amount) throws BusinessException;

    //异步更新库存
    boolean asyncDecreaseStock(Integer courseId, Integer amount);

    //初始化库存流水
    String initStockLog(Integer courseId, Integer amount);

}
