package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.dto.OrderCountDateDTO;
import com.sky.dto.TurnoutDateDTO;
import com.sky.dto.UserDateDTO;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 根据时间段统计营业数据
     *
     * @param begin
     * @param end
     * @return
     */
    public BusinessDataVO getBusinessData(LocalDate begin, LocalDate end) {
        /**
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率：有效订单数 / 总订单数
         * 平均客单价：营业额 / 有效订单数
         * 新增用户：当日新增用户的数量
         */

        //查询总订单数
        Integer totalOrderCount = 0;
        List<OrderCountDateDTO> orderCountDateDTOList = orderMapper.getOrderBydataList(begin, end, null);
        for (OrderCountDateDTO orderCountDateDTO : orderCountDateDTOList) {
            totalOrderCount += orderCountDateDTO.getOrderCount();
        }

        //营业额
        Double turnover = 0.0;
        List<TurnoutDateDTO> turnoutDateDTOList = orderMapper.getTurnoutBydataList(begin, end);
        for (TurnoutDateDTO turnoutDateDTO : turnoutDateDTOList) {
            turnover += turnoutDateDTO.getTotalAmount();
        }

        //有效订单数
        Integer validOrderCount = 0;
        List<OrderCountDateDTO> validOrderCountDateDTOList = orderMapper.getOrderBydataList(begin, end, Orders.COMPLETED);
        for (OrderCountDateDTO validOrderCountDateDTO : validOrderCountDateDTOList) {
            validOrderCount += validOrderCountDateDTO.getOrderCount();
        }

        Double unitPrice = 0.0;

        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0 && validOrderCount != 0) {
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            //平均客单价
            unitPrice = turnover / validOrderCount;
        }

        //新增用户数
        Integer newUsers = 0;
        List<UserDateDTO> userDateDTOList = userMapper.getNewUserBydataList(begin, end);
        for (UserDateDTO userDateDTO : userDateDTOList) {
            newUsers += userDateDTO.getNewUser();
        }

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }


    /**
     * 查询订单管理数据
     *
     * @return
     */
    public OrderOverViewVO getOrderOverView() {
        LocalDate begin = LocalDate.now();

        //待接单
        Integer waitingOrders = 0;
        List<OrderCountDateDTO> waitingOrderCountDateDTOList = orderMapper.getOrderBydataList(begin, begin, Orders.TO_BE_CONFIRMED);
        if (waitingOrderCountDateDTOList != null && !waitingOrderCountDateDTOList.isEmpty()) {
            waitingOrders = waitingOrderCountDateDTOList.get(0).getOrderCount();
        }

        //待派送
        Integer deliveredOrders = 0;
        List<OrderCountDateDTO> deliveredOrderCountDateDTOList = orderMapper.getOrderBydataList(begin, begin, Orders.CONFIRMED);
        if (deliveredOrderCountDateDTOList != null && !deliveredOrderCountDateDTOList.isEmpty()) {
            deliveredOrders = deliveredOrderCountDateDTOList.get(0).getOrderCount();
        }

        //已完成
        Integer completedOrders = 0;
        List<OrderCountDateDTO> completedOrderCountDateDTOList = orderMapper.getOrderBydataList(begin, begin, Orders.COMPLETED);
        if (completedOrderCountDateDTOList != null && !completedOrderCountDateDTOList.isEmpty()) {
            completedOrders = completedOrderCountDateDTOList.get(0).getOrderCount();
        }

        //已取消
        Integer cancelledOrders = 0;
        List<OrderCountDateDTO> cancelledOrderCountDateDTOList = orderMapper.getOrderBydataList(begin, begin,Orders.CANCELLED);
        if (cancelledOrderCountDateDTOList != null && !cancelledOrderCountDateDTOList.isEmpty()) {
            cancelledOrders = cancelledOrderCountDateDTOList.get(0).getOrderCount();
        }

        //全部订单
        Integer allOrders = 0;
        List<OrderCountDateDTO> allOrderCountDateDTOList = orderMapper.getOrderBydataList(begin, begin,null);
        if (allOrderCountDateDTOList != null && !allOrderCountDateDTOList.isEmpty()) {
            allOrders = allOrderCountDateDTOList.get(0).getOrderCount();
        }

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    public DishOverViewVO getDishOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = dishMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = dishMapper.countByMap(map);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    public SetmealOverViewVO getSetmealOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = setmealMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = setmealMapper.countByMap(map);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
