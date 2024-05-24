package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
  @Autowired
  private ShoppingCartMapper shoppingCartMapper;
  @Autowired
  private DishMapper dishMapper;
  @Autowired
  private SetmealMapper setmealMapper;
  /**
   * 添加購物車
   * @param shoppingCartDTO
   */
  @Override
  public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
    // 判斷當前加入到購物車中的商品是否已經存在
    ShoppingCart shoppingCart = new ShoppingCart();
    BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
    Long userId = BaseContext.getCurrentId();
    shoppingCart.setUserId(userId);
    List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
    // 如果已經存在，只需要將數量加一
    if(list != null && list.size() > 0) {
      ShoppingCart cart = list.get(0);
      cart.setNumber(cart.getNumber()+1);
      shoppingCartMapper.updateNumberById(cart);
    }else {
      // 如果不存在，需要插入一條購物車數據
      // 判斷本次添加到購物車的是菜品還是套餐
      Long dishId = shoppingCartDTO.getDishId();
      if(dishId != null) {
        Dish dish = dishMapper.getById(dishId);
        shoppingCart.setName(dish.getName());
        shoppingCart.setImage(dish.getImage());
        shoppingCart.setAmount(dish.getPrice());
      }else{
        // 如果添加得是套餐的話
        Long setmealId = shoppingCartDTO.getSetmealId();
//        Setmeal setmeal = setmealMapper.getById(setmealId);
//        shoppingCart.setName(setmeal.getName());
//        shoppingCart.setImage(setmeal.getImage());
//        shoppingCart.setAmount(setmeal.getPrice());
      }
      shoppingCart.setNumber(1);
      shoppingCart.setCreateTime(LocalDateTime.now());
      shoppingCartMapper.insert(shoppingCart);
    }
  }

  /**
   * 查看購物車
   * @return
   */
  @Override
  public List<ShoppingCart> showShoppingCart() {
    Long userId = BaseContext.getCurrentId();
    ShoppingCart shoppingCart = ShoppingCart.builder()
            .userId(userId)
            .build();
    List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
    return list;
  }
}
