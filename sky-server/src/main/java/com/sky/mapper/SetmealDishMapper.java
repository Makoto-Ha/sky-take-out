package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
  /**
   * 根據菜品id查詢對應的套餐id
   * @param dishIds
   * @return
   */

  List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
