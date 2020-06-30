package com.tsingxin.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.tsingxin.error.BusinessException;
import com.tsingxin.error.EmBusinessError;
import com.tsingxin.mq.MqProducer;
import com.tsingxin.response.CommonReturnType;
import com.tsingxin.service.CourseService;
import com.tsingxin.service.OrderService;
import com.tsingxin.service.GrabService;
import com.tsingxin.service.model.UserModel;
import com.tsingxin.util.CodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(origins = "*", allowCredentials = "true",allowedHeaders = "*")
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private CourseService courseService;

    @Autowired
    private GrabService grabService;

    private ExecutorService executorService;

    private RateLimiter orderCreateRateLimiter;

    @PostConstruct
    public void init(){
        executorService = Executors.newFixedThreadPool(20); //线程池 20个可工作

        orderCreateRateLimiter = RateLimiter.create(300);
    }

    //生成抢课令牌
    @RequestMapping(value = "/generatetoken", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType generatetoken(@RequestParam(name = "courseId")Integer courseId,
                                        @RequestParam(name = "grabId")Integer grabId) throws BusinessException {
        //根据token获取用户信息
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }
        //获取用户的登录信息
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if(userModel == null){ //过期
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        //获取抢课访问令牌
        String grabToken = grabService.generateSecondKillToken(grabId, courseId, userModel.getId());

        if(grabToken == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATTION_ERROR,"生成令牌失败");
        }

        //返回结果
        return CommonReturnType.create(grabToken);

    }


    //封装下单请求
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "courseId")Integer courseId,
                                        @RequestParam(name = "grabId", required = false)Integer grabId,
                                        @RequestParam(name = "amount")Integer amount,
                                        @RequestParam(name = "grabToken", required = false)String grabToken) throws BusinessException {

//        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");

        if(!orderCreateRateLimiter.tryAcquire()){
            throw new BusinessException(EmBusinessError.RATELIMIT);
        }

        //获取用户的登录信息
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isEmpty(token)){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if(userModel == null){ //过期
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        //校验抢课令牌是否正确
        if(grabId != null){
            String inRedisGrabToken = (String) redisTemplate.opsForValue().get("grab_token_" + grabId + "_userId_" + userModel.getId() + "_courseId_" + courseId);
            if(inRedisGrabToken == null){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATTION_ERROR,"抢课令牌校验失败");
            }
            if(!StringUtils.equals(grabToken, inRedisGrabToken)){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATTION_ERROR,"抢课令牌校验失败");
            }
        }

//        if(isLogin == null || !isLogin.booleanValue()){
//            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
//        }

        //获取用户的登录信息
//        UserModel userModel = (UserModel)httpServletRequest.getSession().getAttribute("LOGIN_USER");

//        OrderModel orderModel = orderService.createOrder(userModel.getId(), courseId, grabId, amount);

        //同步调用线程池的submit方法
        //拥塞窗口为20的等待队列，用来队列化泄洪
        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                //加入库存流水init状态
                String stockLogId = courseService.initStockLog(courseId, amount);

                //异步发送事务型消息请求
                if(!mqProducer.transactionAsyncReduceStock(userModel.getId(),courseId,grabId,amount,stockLogId)){
                    throw new BusinessException(EmBusinessError.UNKNOWN_ERROR,"抢课失败");
                }

                return null;
            }
        });

        try {
            future.get();
        } catch (InterruptedException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        } catch (ExecutionException e) {
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }


        /*//加入库存流水init状态
        String stockLogId = courseService.initStockLog(courseId, amount);

        //异步发送事务型消息请求
        if(!mqProducer.transactionAsyncReduceStock(userModel.getId(),courseId,grabId,amount,stockLogId)){
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR,"抢课失败");
        }
*/

        return CommonReturnType.create(null);
    }
}
