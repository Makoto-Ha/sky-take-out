package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
  /**
   * 插入訂單數據
   * @param orders
   */
  void insert(Orders orders);

  /**
   * 根据订单号查询订单
   * @param orderNumber
   */
  @Select("select * from orders where number = #{orderNumber}")
  Orders getByNumber(String orderNumber);

  /**
   * 修改订单信息
   * @param orders
   */
  void update(Orders orders);
  @Select("select count(id) from orders where status = #{status}")
  Integer countStatus(Integer status);

  @Select("select * from orders where status = #{status} and order_time < #{orderTime} ")
  List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);
}