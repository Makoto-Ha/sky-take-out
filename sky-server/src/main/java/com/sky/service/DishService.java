package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
  /**
   * 新增菜品和對應的口味
   * @param dishDTO
   */
  void saveWithFlavor(DishDTO dishDTO);

  /**
   * 菜品分頁查詢
   * @param dishPageQueryDTO
   * @return
   */
  PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

  /**
   * 批量刪除菜品
   * @param ids
   */
  void deleteBatch(List<Long> ids);

  /**
   * 根據id查詢菜品口味數據
   * @param id
   * @return
   */
  DishVO getByIdWithFlavor(Long id);

  /**
   * 根據id修改菜品信息和口味信息
   * @param dishDTO
   */
  void updateWithFlavor(DishDTO dishDTO);

  /**
   * 条件查询菜品和口味
   * @param dish
   * @return
   */
  List<DishVO> listWithFlavor(Dish dish);
}