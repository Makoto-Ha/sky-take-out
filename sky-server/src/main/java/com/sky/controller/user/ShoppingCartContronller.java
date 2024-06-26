package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "C端購物車相關接口")
public class ShoppingCartContronller {
  private ShoppingCartService shoppingCartService;

  /**
   * 添加購物車
   * @return
   */
  @PostMapping("/add")
  @ApiOperation("添加購物車")
  public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
    log.info("添加購物車: {}", shoppingCartDTO);
    shoppingCartService.addShoppingCart(shoppingCartDTO);
    return Result.success();
  }

  @GetMapping("/list")
  @ApiOperation("查看購物車")
  public Result<List<ShoppingCart>> list() {
    List<ShoppingCart> list = shoppingCartService.showShoppingCart();
    return Result.success(list);
  }

  @DeleteMapping("/clean")
  @ApiOperation("清空購物車")
  public Result clean() {
    shoppingCartService.cleanShoppingCart();
    return Result.success();
  }
}
