package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface OrderDetailMapper {

  /**
   * 批量插入訂單明細數據
   * @param orderDetailList
   */
  void insertBatch(ArrayList<OrderDetail> orderDetailList);
}