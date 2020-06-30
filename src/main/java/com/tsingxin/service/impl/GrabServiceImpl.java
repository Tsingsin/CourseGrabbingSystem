package com.tsingxin.service.impl;

import com.tsingxin.dao.GrabDOMapper;
import com.tsingxin.dataobject.GrabDO;
import com.tsingxin.service.CourseService;
import com.tsingxin.service.GrabService;
import com.tsingxin.service.UserService;
import com.tsingxin.service.model.CourseModel;
import com.tsingxin.service.model.GrabModel;
import com.tsingxin.service.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class GrabServiceImpl implements GrabService {

    @Autowired
    private GrabDOMapper grabDOMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Override
    public GrabModel getGrabByCourseId(Integer courseId) {

        //获取对应课程的抢课活动信息
        GrabDO grabDO = grabDOMapper.selectByCourseId(courseId);

        //dataobject -> model
        GrabModel grabModel = convertFromDataObject(grabDO);
        if(grabModel == null){
            return null;
        }

        //判断当前时间是否抢课即将开始 或 正在进行
        if(grabModel.getStartDate().isAfterNow()){
            grabModel.setStatus(1);
        }else if(grabModel.getEndDate().isBeforeNow()){
            grabModel.setStatus(3);
        }else {
            grabModel.setStatus(2);
        }

        return grabModel;
    }

    @Override
    public void publishGrab(Integer grabId) {
        //通过活动id获取活动
        GrabDO grabDO = grabDOMapper.selectByPrimaryKey(grabId);
        if(grabDO.getCourseId()==null || grabDO.getCourseId().intValue()==0){
            return;
        }
        CourseModel courseModel = courseService.getCourseById(grabDO.getCourseId());

        //将库存同步到redis内
        redisTemplate.opsForValue().set("grab_course_stock_" + courseModel.getId(), courseModel.getStock());

        //将大闸的限制数字设到redis内
        redisTemplate.opsForValue().set("grab_door_count_" + grabId, courseModel.getStock().intValue() * 5);
    }

    @Override
    public String generateSecondKillToken(Integer grabId, Integer courseId, Integer userId) {

        //判断库存是否售罄，若售罄key存在，则直接返回抢课失败
        if(redisTemplate.hasKey("grab_course_stock_invalid_"+courseId)){
            return null;
        }

        //获取对应课程的抢课活动信息
        GrabDO grabDO = grabDOMapper.selectByPrimaryKey(grabId);

        //dataobject -> model
        GrabModel grabModel = convertFromDataObject(grabDO);
        if(grabModel == null){
            return null;
        }

        //判断当前时间是否抢课活动即将开始 或 正在进行
        if(grabModel.getStartDate().isAfterNow()){
            grabModel.setStatus(1);
        }else if(grabModel.getEndDate().isBeforeNow()){
            grabModel.setStatus(3);
        }else {
            grabModel.setStatus(2);
        }

        //判断抢课是否正在进行
        if(grabModel.getStatus().intValue() != 2 ){
            return null;
        }
        //判断course信息是否存在
        CourseModel courseModel = courseService.getCourseByIdInCache(courseId);
        if(courseModel == null){
            return null;
        }
        //判断用户是否存在
        UserModel userModel = userService.getUserByIdInCache(userId);
        if(userModel == null){
            return null;
        }

        //获取抢课大闸的count数量
        long result = redisTemplate.opsForValue().increment("grab_door_count_" + grabId, -1);
        if(result < 0){
            return null;
        }

        //生成token，存入redis，5分钟有效
        String token = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set("grab_token_" + grabId + "_userId_" + userId + "_courseId_" + courseId, token);
        redisTemplate.expire("grab_token_" + grabId,5, TimeUnit.MINUTES);

        return token;
    }

    private GrabModel convertFromDataObject(GrabDO grabDO){
        if(grabDO == null){
            return null;
        }

        GrabModel grabModel = new GrabModel();
        BeanUtils.copyProperties(grabDO, grabModel);
        grabModel.setStartDate(new DateTime(grabDO.getStartDate()));
        grabModel.setEndDate(new DateTime(grabDO.getEndDate()));

        return grabModel;
    }
}
