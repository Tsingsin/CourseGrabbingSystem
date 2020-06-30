package com.tsingxin.service.impl;

import com.tsingxin.dao.CourseDOMapper;
import com.tsingxin.dao.CourseStockDOMapper;
import com.tsingxin.dao.StockLogDOMapper;
import com.tsingxin.dataobject.CourseStockDO;
import com.tsingxin.dataobject.StockLogDO;
import com.tsingxin.dataobject.CourseDO;
import com.tsingxin.error.BusinessException;
import com.tsingxin.error.EmBusinessError;
import com.tsingxin.mq.MqProducer;
import com.tsingxin.service.CourseService;

import com.tsingxin.service.GrabService;
import com.tsingxin.service.model.CourseModel;
import com.tsingxin.service.model.GrabModel;
import com.tsingxin.validator.ValidationResult;
import com.tsingxin.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private CourseDOMapper courseDoMapper;

    @Autowired
    private CourseStockDOMapper courseStockDOMapper;

    @Autowired
    private GrabService grabService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    private CourseDO convertCourseDoFromCourseModel(CourseModel courseModel){
        if (courseModel == null) {
            return null;
        }
        CourseDO courseDo = new CourseDO();
        BeanUtils.copyProperties(courseModel, courseDo);
        return courseDo;
    }

    private CourseStockDO convertCourseStockDOFromCourseModel(CourseModel courseModel){
        if (courseModel == null) {
            return null;
        }
        CourseStockDO courseStockDO = new CourseStockDO();
        courseStockDO.setCourseId(courseModel.getId());
        courseStockDO.setStock(courseModel.getStock());
        return courseStockDO;
    }

    @Override
    @Transactional
    public CourseModel creatCourse(CourseModel courseModel) throws BusinessException {

        //校验入参
        ValidationResult result = validator.validate(courseModel);
        if (result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATTION_ERROR,result.getErrMsg());
        }

        //转化coursemodel -> dataobject
        CourseDO courseDo = this.convertCourseDoFromCourseModel(courseModel);

        //写入数据库
        courseDoMapper.insertSelective(courseDo);
        courseModel.setId(courseDo.getId());

        CourseStockDO courseStockDO = this.convertCourseStockDOFromCourseModel(courseModel);

        courseStockDOMapper.insertSelective(courseStockDO);

        //返回创建完成的对象
        return this.getCourseById(courseModel.getId());
    }

    @Override
    public List<CourseModel> listCourse() {
        List<CourseDO> courseDoList = courseDoMapper.listCourse();
        List<CourseModel> courseModelList = courseDoList.stream().map(courseDo -> {
            CourseStockDO courseStockDO = courseStockDOMapper.selectByCourseId(courseDo.getId());
            CourseModel courseModel = this.convertModelFromDataObject(courseDo, courseStockDO);
            return courseModel;
        }).collect(Collectors.toList());
        return courseModelList;
    }

    @Override
    public CourseModel getCourseById(Integer id) {
        CourseDO courseDo = courseDoMapper.selectByPrimaryKey(id);
        if (courseDo == null) {
            return null;
        }
        //操作获得库存数量
        CourseStockDO courseStockDO = courseStockDOMapper.selectByCourseId(courseDo.getId());

        //将dataobject -> model
        CourseModel courseModel = convertModelFromDataObject(courseDo, courseStockDO);

        //获取课程信息
        GrabModel grabModel = grabService.getGrabByCourseId(courseModel.getId());
        if(grabModel != null && grabModel.getStatus().intValue() != 3){
            courseModel.setGrabModel(grabModel);
        }

        return courseModel;
    }

    @Override
    public CourseModel getCourseByIdInCache(Integer id) {

        CourseModel courseModel = (CourseModel) redisTemplate.opsForValue().get("course_validate_" + id);
        if(courseModel == null){
            courseModel = this.getCourseById(id);
            redisTemplate.opsForValue().set("course_validate_" + id, courseModel);
            redisTemplate.expire("course_validate_" + id, 10, TimeUnit.MINUTES);
        }

        return courseModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer courseId, Integer amount) throws BusinessException {
//        int affectedRow = courseStockDOMapper.decreaseStock(courseId, amount);
        long result = redisTemplate.opsForValue().increment("grab_course_stock_" + courseId,amount.intValue()*-1);

//        if(affectedRow > 0){
        if(result > 0){
            //更新库存成功
//            boolean mqResult = mqProducer.asyncReduceStock(courseId, amount);
//            if(!mqResult){
//                redisTemplate.opsForValue().increment("grab_course_stock_" + courseId,amount.intValue());
//                return false;
//            }
            return true;
        }else if(result == 0){
            //打上库存售罄标识（redis的key）
            redisTemplate.opsForValue().set("grab_course_stock_invalid_"+courseId, "true");
            //更新库存成功
            return true;
        }else  {
            //更新库存失败
//            redisTemplate.opsForValue().increment("grab_course_stock_" + courseId,amount.intValue());
            this.increaseStock(courseId, amount);
            return false;
        }
    }

    @Override
    public boolean increaseStock(Integer courseId, Integer amount) throws BusinessException {
        redisTemplate.opsForValue().increment("grab_course_stock_" + courseId,amount.intValue());
        return true;
    }

    @Override
    public boolean asyncDecreaseStock(Integer courseId, Integer amount) {
        boolean mqResult = mqProducer.asyncReduceStock(courseId, amount);
        return mqResult;

    }

    private CourseModel convertModelFromDataObject(CourseDO courseDo, CourseStockDO courseStockDO){
        CourseModel courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDo, courseModel);
        courseModel.setStock(courseStockDO.getStock());

        return courseModel;
    }

    //初始化对应的库存流水
    @Override
    @Transactional
    public String initStockLog(Integer courseId, Integer amount) {
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setCourseId(courseId);
        stockLogDO.setAmount(amount);
        stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-", ""));
        stockLogDO.setStatus(1); //初始状态

        stockLogDOMapper.insertSelective(stockLogDO);

        return stockLogDO.getStockLogId();
    }
}
