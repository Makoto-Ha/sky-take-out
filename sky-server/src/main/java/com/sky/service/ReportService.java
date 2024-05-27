package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
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

  /**
   * 統計指定時間區間內的用戶數據
   * @param begin
   * @param end
   * @return
   */
  UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

  /**
   *統計指定時間區間內的訂單數據
   * @param begin
   * @param end
   * @return
   */
  OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

  /**
   *統計指定區間內的銷量排名前10
   * @param begin
   * @param end
   * @return
   */
  SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end);
}
