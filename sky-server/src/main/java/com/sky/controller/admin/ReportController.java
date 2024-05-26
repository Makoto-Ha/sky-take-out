package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "數據統計相關接口")
@Slf4j
public class ReportController {
  @Autowired
  private ReportService reportService;

  @GetMapping("turnOverStatistics")
  @ApiOperation("營業額統計")
  public Result<TurnoverReportVO> turnOverStatistics(
          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
    log.info("營業額數據統計: {}, {}", begin, end);
    return Result.success(reportService.getTurnoverStatistics(begin, end));
  }

  /**
   * 用戶統計
   * @param begin
   * @param end
   * @return
   */
  @GetMapping("/userStatistics")
  @ApiOperation("用戶統計")
  public Result<UserReportVO> userStatistics(
          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
    log.info("用戶數據統計: {}, {}", begin, end);
    return Result.success(reportService.getUserStatistics(begin, end));
  }
}
