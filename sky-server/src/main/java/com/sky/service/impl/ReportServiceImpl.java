package com.sky.service.impl;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<TurnoutDateDTO> turnoutDataList = orderMapper.getTurnoutBydataList(begin, end);
        Map<LocalDate, Double> turnoutDataMap = turnoutDataList.stream().collect(Collectors.toMap(
                TurnoutDateDTO::getOrderDate,
                TurnoutDateDTO::getTotalAmount
        ));

        List<LocalDate> dateList = new ArrayList<>();
        List<Double> turnoutList = new ArrayList<>();
        while(!begin.equals(end.plusDays(1))){
            dateList.add(begin);
            turnoutList.add(turnoutDataMap.getOrDefault(begin, 0.0));
            begin = begin.plusDays(1);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoutList, ","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<UserDateDTO> turnoutDataList = userMapper.getNewUserBydataList(begin, end);
        Map<LocalDate, Integer> newUserDataMap = turnoutDataList.stream().collect(Collectors.toMap(
                UserDateDTO::getCreateDate,
                UserDateDTO::getNewUser
        ));

        Integer totalBegin = userMapper.getTotalByBeginDate(begin);
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        while(!begin.equals(end.plusDays(1))){
            dateList.add(begin);
            int newUserNum = newUserDataMap.getOrDefault(begin, 0);
            newUserList.add(newUserNum);
            totalBegin += newUserNum;
            totalUserList.add(totalBegin);
            begin = begin.plusDays(1);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<OrderCountDateDTO> orderDateDTOList = orderMapper.getOrderBydataList(begin, end, null);
        Map<LocalDate, Integer> orderDataMap = orderDateDTOList.stream().collect(Collectors.toMap(
                OrderCountDateDTO::getOrderDate,
                OrderCountDateDTO::getOrderCount
        ));

        List<OrderCountDateDTO> validOrderDateDTOList = orderMapper.getOrderBydataList(begin, end, Orders.COMPLETED);
        Map<LocalDate, Integer> validOrderDataMap = validOrderDateDTOList.stream().collect(Collectors.toMap(
                OrderCountDateDTO::getOrderDate,
                OrderCountDateDTO::getOrderCount
        ));

        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        while(!begin.equals(end.plusDays(1))){
            dateList.add(begin);
            orderCountList.add(orderDataMap.getOrDefault(begin, 0));
            validOrderCountList.add(validOrderDataMap.getOrDefault(begin, 0));
            begin = begin.plusDays(1);
        }


        int totalOrderCount = orderCountList.stream().reduce(Integer::sum).orElse(0);
        int validOrderCount = validOrderCountList.stream().reduce(Integer::sum).orElse(0);

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate((double) validOrderCount /totalOrderCount)
                .build();
    }

    /**
     * 指定时间范围内的销量排名top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(begin, end);
        List<String> nameList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }
}
