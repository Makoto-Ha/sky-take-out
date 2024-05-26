package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
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
}
