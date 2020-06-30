package com.tsingxin.service;

import com.tsingxin.error.BusinessException;
import com.tsingxin.service.model.OrderModel;

public interface OrderService {
    //1.通过前端url上传过来抢课id，然后下单接口内校验对应id是否属于对应课程且活动已开始（使用）
    //2.直接在下单接口内判断对应的课程是否存在抢课活动，若存在进行中的 则可以抢课
    OrderModel createOrder(Integer userId, Integer courseId, Integer grabId, Integer amount, String stockLogId) throws BusinessException;

}
