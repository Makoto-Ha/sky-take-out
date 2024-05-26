package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public interface ReportService {
  /**
   * 統計指定時間區間內的營業額數據
   * @param begin
   * @param end
   * @return
   */
  TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
