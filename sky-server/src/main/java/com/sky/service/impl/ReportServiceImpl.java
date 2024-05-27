package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.mbeans.UserMBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
  @Autowired
  private OrderMapper orderMapper;
  @Autowired
  private UserMapper userMapper;
  @Autowired
  private WorkspaceService workspaceService;
  /**
   * 統計指定時間區間內的營業額數據
   * @param begin
   * @param end
   * @return
   */
  @Override
  public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
    // 當前集合用於存放從begin到end範圍內的每天的日期
    List<LocalDate> dateList = new ArrayList<>();
    dateList.add(begin);

    while(!begin.equals(end)) {
      // 計算指定日期後一天的對應的日期
      begin = begin.plusDays(1);
      dateList.add(begin);
    }
    List<Double> turnoverList = new ArrayList<>();
    for (LocalDate date : dateList) {
      //查詢date日期對應的營業額數據，營業額是指: 狀態為"已完成"的訂單金額合計
      LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
      LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
      Map map = new HashMap<>();
      map.put("begin", beginTime);
      map.put("end", endTime);
      map.put("status", Orders.COMPLETED);
      Double turnover = orderMapper.sumByMap(map);
      turnover = turnover == null ? 0.0 : turnover;
      turnoverList.add(turnover);
    }

    // 封裝返回結果
    return TurnoverReportVO.builder()
                           .dateList(StringUtils.join(dateList, ","))
                           .turnoverList(StringUtils.join(turnoverList, ","))
                           .build();
  }

  /**
   * 統計指定時間區間內的用戶數據
   * @param begin
   * @param end
   * @return
   */
  @Override
  public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
    // 存放從begin到end之間的每天對應的日期
    List<LocalDate> dateList = new ArrayList<>();
    dateList.add(begin);

    while(!begin.equals(end)) {
      begin = begin.plusDays(1);
      dateList.add(begin);
    }

     // 存放每天的新增用戶數量
     List<Integer> newUserList = new ArrayList<>();
     // 存放每天的總用戶數量
     List<Integer> totalUserList = new ArrayList<>();

    for (LocalDate date : dateList) {
      LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
      LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

      Map map = new HashMap<>();
      map.put("end", beginTime);
      // 總用戶數量
      Integer totalUser = userMapper.countByMap(map);

      map.put("begin", beginTime);

      // 新增用戶數量
      Integer newUser = userMapper.countByMap(map);

      totalUserList.add(totalUser);
      newUserList.add(newUser);
    }

    return UserReportVO.builder()
            .dateList(StringUtils.join(dateList, ","))
            .totalUserList(StringUtils.join(totalUserList, ","))
            .newUserList(StringUtils.join(newUserList, ","))
            .build();
  }

  /**
   *統計指定時間區間內的訂單數據
   * @param begin
   * @param end
   * @return
   */
  @Override
  public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
    // 存放從begin到end之間的每天對應的日期
    List<LocalDate> dateList = new ArrayList<>();
    dateList.add(begin);

    while(!begin.equals(end)) {
      begin = begin.plusDays(1);
      dateList.add(begin);
    }
    // 存放每天訂單總數
    List<Integer> orderCountList = new ArrayList<>();
    // 存放每天有效訂單數
    List<Integer> validOrderCountList = new ArrayList<>();

    // 遍歷dateList集合，查詢每天的有效訂單數和訂單總數
    for (LocalDate date : dateList) {
      // 查詢每天的訂單總數
      LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
      LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
      Integer orderCount = getOrderCount(beginTime, endTime, null);
      // 查詢每天的有效訂單數
      Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

      orderCountList.add(orderCount);
      validOrderCountList.add(validOrderCount);
    }

    Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

    Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

    // 計算訂單完成率
    Double orderCompletetionRate = totalOrderCount != 0.0 ? validOrderCount.doubleValue() / totalOrderCount : 0.0;

    return OrderReportVO.builder()
          .dateList(StringUtils.join(dateList, ","))
          .orderCountList(StringUtils.join(orderCountList, ","))
          .validOrderCountList(StringUtils.join(validOrderCountList, ","))
          .totalOrderCount(totalOrderCount)
          .validOrderCount(validOrderCount)
          .orderCompletionRate(orderCompletetionRate)
          .build();
  }

  /**
   * 根據條件統計訂單數量
   * @param begin
   * @param end
   * @param status
   * @return
   */
  private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
      Map map = new HashMap<>();
      map.put("begin", begin);
      map.put("end", end);
      map.put("status", status);

    return orderMapper.countByMap(map);
  }

  /**
   *統計指定區間內的銷量排名前10
   * @param begin
   * @param end
   * @return
   */

  @Override
  public SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end) {
    LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
    LocalDateTime endTime = LocalDateTime.of(begin, LocalTime.MAX);

    List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
    List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
    String nameList = StringUtils.join(names, ",");

    List<String> numbers = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
    String numberList = StringUtils.join(numbers, ",");
    return SalesTop10ReportVO.builder().nameList(nameList).numberList(numberList).build();
  }

  /**
   * 導出運營數據報表
   * @param response
   */
  @Override
  public void exportBusinessDate(HttpServletResponse response) {
    // 查詢數據庫，獲取營業數據---查詢最近30天
    LocalDate dateBegin = LocalDate.now().minusDays(30);
    LocalDate dateEnd = LocalDate.now().minusDays(1);
    ;

    // 查詢概覽數據
    BusinessDataVO businessDataVO = workspaceService.getBusinessData(
            LocalDateTime.of(dateBegin, LocalTime.MIN),
            LocalDateTime.of(dateEnd, LocalTime.MAX)
    );
    // 通過POI將數據寫入到Excel文件中
    InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
    // 基於模板文件創建一個新的Excel文件
    try {
      XSSFWorkbook excel = new XSSFWorkbook(in);

      // 獲取表格文件的Sheet頁
      XSSFSheet sheet = excel.getSheet("Sheet1");

      //填充數據--時間
      sheet.getRow(1).getCell(1).setCellValue("時間: " + dateBegin + "至" + dateEnd);

      // 獲取第四行
      XSSFRow row = sheet.getRow(3);
      row.getCell(2).setCellValue(businessDataVO.getTurnover());
      row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
      row.getCell(6).setCellValue(businessDataVO.getNewUsers());

      // 獲取第五行
      row = sheet.getRow(4);
      row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
      row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

      // 填充明細數據
      for (int i = 0; i < 30; i++) {
        LocalDate date = dateBegin.plusDays(i);
        // 查詢某一天的營業數據
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
        // 獲得某一行
        row = sheet.getRow(7+i);
        row.getCell(1).setCellValue(date.toString());
        row.getCell(2).setCellValue(businessData.getTurnover());
        row.getCell(3).setCellValue(businessData.getValidOrderCount());
        row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
        row.getCell(5).setCellValue(businessData.getUnitPrice());
        row.getCell(6).setCellValue(businessData.getNewUsers());
      }

      // 通過輸出流將Excel文件下載到客戶端瀏覽器
      ServletOutputStream out = response.getOutputStream();
      excel.write(out);
      out.close();
      excel.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
