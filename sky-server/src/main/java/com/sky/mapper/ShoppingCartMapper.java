package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
  /**
   * 動態條件查詢
   * @param shoppingCart
   * @return
   */
  List<ShoppingCart> list(ShoppingCart shoppingCart);

  /**
   * 根據id修改商品數量
   * @param shoppingCart
   */
  @Update("update shopping_cart set number = #{number} where id = #{id}")
  void updateNumberById(ShoppingCart shoppingCart);

  /**
   * 插入購物車數據
   * @param shoppingCart
   */
  @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time)" +
          "values (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{amount}, #{createTime})")
  void insert(ShoppingCart shoppingCart);

  /**
   * 根據用戶id刪除購物車數據
   * @param userId
   */
  @Delete("delete from shopping_cart where user_id = #{userId}")
  void deleteByUserId(Long userId);
}