package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.mbeans.UserMBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
  @Autowired
  private OrderMapper orderMapper;
  @Autowired
  private UserMapper userMapper;
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
}
