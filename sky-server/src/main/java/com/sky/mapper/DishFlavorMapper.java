package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
  /**
   * 批量插入口味數據
   * @param flavors
   */
  void insertBatch(List<DishFlavor> flavors);

  /**
   * 根據菜品id刪除對應的口味數據
   * @param dishId
   */
  @Delete("delete from dish_flavor where dish_id = #{dishId}")
  void deleteByDishId(Long dishId);

  /**
   * 根據id集合批量刪除數據
   *
   * @param dishIds
   */
  void deleteByDishIds(List<Long> dishIds);
}
