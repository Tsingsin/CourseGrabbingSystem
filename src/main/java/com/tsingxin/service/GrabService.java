package com.tsingxin.service;

import com.tsingxin.service.model.GrabModel;


public interface GrabService {

    //根据courseid获取即将进行的或正在进行的抢课活动
    GrabModel getGrabByCourseId(Integer courseId);

    //抢课活动发布,缓存库存
    void publishGrab(Integer grabId);

    //生成抢课令牌
    String generateSecondKillToken(Integer grabId, Integer courseId, Integer userId);

}
