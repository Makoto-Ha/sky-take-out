package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "用戶端訂單相關接口")
public class OrderController {
  @Autowired
  private OrderService orderService;

  /**
   * 用戶下單
   * @param ordersSubmitVO
   * @return
   */
  @PostMapping("/submit")
  @ApiOperation("用戶下單")
  public Result<OrderSubmitVO> submit(@RequestBody OrderSubmitVO ordersSubmitVO) {
    log.info("用戶下單: {}", ordersSubmitVO);
    OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitVO);
    return Result.success(orderSubmitVO);
  }
}
