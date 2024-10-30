package com.sky.service.impl;

import com.sky.dto.TurnoutDateDTO;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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
}
