package com.tsingxin.service.impl;

import com.tsingxin.dao.OrderDOMapper;
import com.tsingxin.dao.SequenceDOMapper;
import com.tsingxin.dao.StockLogDOMapper;
import com.tsingxin.dataobject.OrderDO;
import com.tsingxin.dataobject.SequenceDO;
import com.tsingxin.dataobject.StockLogDO;
import com.tsingxin.error.BusinessException;
import com.tsingxin.error.EmBusinessError;
import com.tsingxin.service.CourseService;
import com.tsingxin.service.OrderService;
import com.tsingxin.service.UserService;
import com.tsingxin.service.model.CourseModel;
import com.tsingxin.service.model.OrderModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer courseId, Integer grabId, Integer amount, String stockLogId) throws BusinessException {

        //1.校验下单状态：下单的状态是否存在，用户是否合法，数量是否正确
//        CourseModel courseModel = courseService.getCourseById(courseId);
        CourseModel courseModel = courseService.getCourseByIdInCache(courseId);
        if(courseModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATTION_ERROR,"课程信息不存在");
        }


        //生成token已验证
        /*
//        UserModel userModel = userService.getUserById(userId);
        UserModel userModel = userService.getUserByIdInCache(userId);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATTION_ERROR,"用户信息不存在");
        }*/

        if(amount <= 0 || amount > 99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATTION_ERROR,"数量信息不正确");
        }

       /* //校验活动信息
        if(grabId != null){
            //(1)校验对应活动是否存在这个适用商品
            if(grabId.intValue() != courseModel.getGrabModel().getId()){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATTION_ERROR,"活动信息不正确");
                //(2)校验活动是否正在进行中
            }else if(courseModel.getGrabModel().getStatus().intValue() != 2){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATTION_ERROR,"活动还未开始");
            }

        }*/

        //2.落单减库存（另一种是支付减库存，有超卖风险）
        boolean result = courseService.decreaseStock(courseId, amount);
        if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setCourseId(courseId);
        orderModel.setAmount(amount);
        orderModel.setGrabId(grabId);

        //生成交易流水号（订单号）
        orderModel.setId(generateOrderNo());

        OrderDO orderDO = convertFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //设置库存流水状态为成功
        StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
        if(stockLogDO == null){
            throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
        }
        stockLogDO.setStatus(2);
        stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);

//        try { //模拟order卡住
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
//            @Override
//            public void afterCommit() { //在最近的Transactional标签成功commit后执行
//                //异步更新库存
//                boolean mqResult = courseService.asyncDecreaseStock(courseId, amount);
//                if(!mqResult){
//                    courseService.increaseStock(courseId, amount);
//                    throw new BusinessException(EmBusinessError.MQ_SEND_FAIL);
//                }
//            }
//        });

        //4.返回前端
        return orderModel;
    }

    //不管整个事务中哪里回滚，都会开启一个事务提交
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderNo(){
        //订单号有16位
        StringBuilder stringBuilder = new StringBuilder();

        //前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);

        //中间6位为自增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);

        String sequenceStr = String.valueOf(sequence);
        for(int i = 0; i < 6-sequenceStr.length(); i++){ //可能会超过6位数
            stringBuilder.append("0");
        }
        stringBuilder.append(sequenceStr);

        //最后2位为分库分表位，水平拆分(暂时写死)
        stringBuilder.append("00");
        return stringBuilder.toString();
    }

    private OrderDO convertFromOrderModel(OrderModel orderModel){
        if(orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel, orderDO);
        return orderDO;
    }
}
