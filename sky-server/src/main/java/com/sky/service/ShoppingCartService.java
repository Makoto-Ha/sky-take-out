package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

public interface ShoppingCartService {
  /**
   * 添加購物車
   * @param shoppingCartDTO
   */
  void addShoppingCart(ShoppingCartDTO shoppingCartDTO);


}
