package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

  /**
   * 插入菜品數據
   * @param dish
   */
  @AutoFill(value = OperationType.INSERT)
  void insert(Dish dish);

  Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

  /**
   * 根據主鍵查詢菜品
   * @param id
   */
  @Select("select * from dish where id = #{id}")
  Dish getById(Long id);

  /**
   * 根據主鍵刪除菜品數據
   * @param id
   */
  @Delete("delete from dish where id = #{id}")
  void deleteById(Long id);

  /**
   * 根據id集合批量刪除數據
   * @param ids
   */
  void deleteByIds(List<Long> ids);

  /**
   * 根據dishId查詢對應的口味數據
   * @param dishId
   * @return
   */
  @Select("select * from dish_flavor where dish_id = #{dishId}")
  List<DishFlavor> getByDishId(Long dishId);

  /**
   * 根據id動態修改菜品
   * @param dish
   */
  @AutoFill(value = OperationType.UPDATE)
  void update(Dish dish);
}