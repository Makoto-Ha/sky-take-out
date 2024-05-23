package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "菜品管理")
@RequestMapping("/admin/dish")
public class DishController {
  @Autowired
  private DishService dishService;

  @PostMapping
  @ApiOperation("新增菜品")
  public Result save(@RequestBody DishDTO dishDTO) {
    log.info("新增菜品, {}", dishDTO);
    dishService.saveWithFlavor(dishDTO);
    return Result.success();
  }

  /**
   * 菜品分頁查詢
   * @param dishPageQueryDTO
   * @return
   */
  @GetMapping("/page")
  @ApiOperation("菜品分頁查詢")
  public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
    log.info("菜品分頁查詢: {}", dishPageQueryDTO);
    PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
    return Result.success(pageResult);
  }

  @DeleteMapping
  @ApiOperation("批量刪除菜品")
  public Result delete(@RequestParam List<Long> ids) {
    log.info("批量刪除菜品: {}", ids);
    dishService.deleteBatch(ids);
    return Result.success();
  }

  /**
   * 根據id查詢菜品
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  @ApiOperation("根據id查詢菜品")
  public Result<DishVO> getById(@PathVariable Long id) {
    log.info("根據id查詢菜品: {}", id);
    DishVO dishVO = dishService.getByIdWithFlavor(id);
    return Result.success(dishVO);
  }
  @PutMapping
  @ApiOperation("修改菜品")
  public Result update(@RequestBody DishDTO dishDTO) {
    log.info("修改菜品: {}", dishDTO);
    dishService.updateWithFlavor(dishDTO);
    return Result.success();
  }
}