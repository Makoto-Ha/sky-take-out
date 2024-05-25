package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
  @Autowired
  private OrderMapper orderMapper;

  @Scheduled(cron = "0 * * * * * ?")
  public void processTimeoutOrder() {
    log.info("定時處理超時訂單: {}", LocalDateTime.now());

    LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

    List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
    if(orderList != null && orderList.size() > 0) {
      for(Orders orders : orderList) {
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("訂單超時，自動取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
      }
    }
  }

  @Scheduled(cron = "0 0 1 * * ?")
  public void processDeliveryOrder() {
    log.info("定時處理處於派送中的訂單: {}", LocalDateTime.now());

    LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
    List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);
    if(orderList != null && orderList.size() > 0) {
      for(Orders orders : orderList) {
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
      }
    }
  }
}
