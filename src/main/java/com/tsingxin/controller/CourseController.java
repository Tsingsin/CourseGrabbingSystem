package com.tsingxin.controller;

import com.tsingxin.controller.viewobject.CourseVO;
import com.tsingxin.error.BusinessException;
import com.tsingxin.response.CommonReturnType;
import com.tsingxin.service.CacheService;
import com.tsingxin.service.CourseService;
import com.tsingxin.service.GrabService;
import com.tsingxin.service.model.CourseModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller("/course")
@RequestMapping("/course")
@CrossOrigin(origins = "*", allowCredentials = "true",allowedHeaders = "*")
public class CourseController extends BaseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private RedisTemplate redisTemplate; //一个spring bean，封装了key value

    @Autowired
    private CacheService cacheService;

    @Autowired
    private GrabService grabService;

    //创建课程的controller
    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createCourse(@RequestParam(name = "title")String title,
                                       @RequestParam(name = "description")String description,
                                       @RequestParam(name = "credit")Integer credit,
                                       @RequestParam(name = "stock")Integer stock,
                                       @RequestParam(name = "mode")String mode,
                                       @RequestParam(name = "faculty")String faculty,
                                       @RequestParam(name = "teacher")String teacher,
                                       @RequestParam(name = "place")String place,
                                       @RequestParam(name = "time")String time) throws BusinessException {
        //封装service请求 用来创建课程
        CourseModel courseModel = new CourseModel();
        courseModel.setTitle(title);
        courseModel.setDescription(description);
        courseModel.setCredit(credit);
        courseModel.setStock(stock);
        courseModel.setMode(mode);
        courseModel.setFaculty(faculty);
        courseModel.setTeacher(teacher);
        courseModel.setPlace(place);
        courseModel.setTime(time);

        CourseModel courseModelForReturn = courseService.creatCourse(courseModel);

        CourseVO courseVO = convertVOFromModel(courseModelForReturn);

        return CommonReturnType.create(courseVO);
    }

    @RequestMapping(value = "/publishgrab", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType publishgrab(@RequestParam(name = "id")Integer id){
        grabService.publishGrab(id);
        return CommonReturnType.create(null);
    }

    //课程详情浏览
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getCourse(@RequestParam(name = "id")Integer id){
        CourseModel courseModel = null;

        //先取本地缓存
        courseModel = (CourseModel) cacheService.getFromCommonCache("course_"+id);

        if(courseModel == null){
            //根据课程id到redis内获取
            courseModel = (CourseModel) redisTemplate.opsForValue().get("course_"+id);

            //若redis内不存在对应的courseModel，则访问下游service
            if(courseModel == null){
                courseModel = courseService.getCourseById(id); //取数据库
                //设置courseModel到redis内
                redisTemplate.opsForValue().set("course_"+id, courseModel);
                redisTemplate.expire("course_"+id, 10, TimeUnit.MINUTES);
            }

            //填充本地缓存
            cacheService.setCommonCache("course_"+id, courseModel);
        }

        CourseVO courseVO = convertVOFromModel(courseModel);

        return CommonReturnType.create(courseVO);
    }

    //课程列表页面浏览
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listCourse(){
        List<CourseModel> courseModelList = courseService.listCourse();

        //使用stream api将list内的courseModel转化为courseVO
        List<CourseVO> courseVOList = courseModelList.stream().map(courseModel -> {
            CourseVO courseVO = this.convertVOFromModel(courseModel);
            return courseVO;
        }).collect(Collectors.toList());

        return CommonReturnType.create(courseVOList);
    }

    private CourseVO convertVOFromModel(CourseModel courseModel){
        if (courseModel == null) {
            return null;
        }
        CourseVO courseVO = new CourseVO();
        BeanUtils.copyProperties(courseModel, courseVO);

        if(courseModel.getGrabModel() != null){
            //有正在进行或即将进行的抢课活动
            courseVO.setGrabStatus(courseModel.getGrabModel().getStatus());
            courseVO.setGrabId(courseModel.getGrabModel().getId());
            courseVO.setStartDate(courseModel.getGrabModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:SS")));
        }else{
            courseVO.setGrabStatus(0);
        }

        return courseVO;
    }
}
