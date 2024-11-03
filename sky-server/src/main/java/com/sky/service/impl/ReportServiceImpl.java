package com.sky.service.impl;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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

    @Autowired
    private WorkspaceService workspaceService;

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

    /**
     * 导出运营数据报表
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // 查询数据库，获取营业数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(dateBegin, dateEnd);
        // 通过POI将数据写入Excel文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            // 基于模板问价创建一个新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            // 获取表格文件的sheet页
            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            // 填充数据-时间
            sheet1.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);
            XSSFRow row = sheet1.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            row = sheet1.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            // 填充30天明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                BusinessDataVO businessData = workspaceService.getBusinessData(date, date);
                // 填充每一行
                row = sheet1.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            // 通过输出流将excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            // 关闭资源
            out.close();
            excel.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
