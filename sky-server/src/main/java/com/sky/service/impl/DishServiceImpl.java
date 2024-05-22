package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
  @Autowired
  private DishMapper dishMapper;
  @Autowired
  private DishFlavorMapper dishFlavorMapper;
  @Autowired
  private SetmealDishMapper setmealDishMapper;

  /**
   * 新增菜品和對應的口味
   * @param dishDTO
   */
  @Override
  @Transactional
  public void saveWithFlavor(DishDTO dishDTO) {
    Dish dish = new Dish();
    BeanUtils.copyProperties(dishDTO, dish);
    dishMapper.insert(dish);
    // 獲取insert生成的主鍵值
    Long dishId = dish.getId();

    List<DishFlavor> flavors = dishDTO.getFlavors();
    if(flavors != null && flavors.size() > 0) {
      flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
      dishFlavorMapper.insertBatch(flavors);
    }
  }

  /**
   * 菜品分頁查詢
   * @param dishPageQueryDTO
   * @return
   */
  @Override
  public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
    PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
    Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
    return new PageResult(page.getTotal(), page.getResult());
  }

  /**
   * 批量刪除菜品
   * @param ids
   */
  @Transactional
  @Override
  public void deleteBatch(List<Long> ids) {
    // 判斷當前菜品是否能夠刪除--是否存在起售中的菜品
    for (Long id : ids) {
      Dish dish = dishMapper.getById(id);
      if(dish.getStatus() == StatusConstant.ENABLE) {
        // 當前菜品處於起售中，不能刪除
        throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
      }
    }

    //判斷當前菜品是否能夠刪除--是否被套餐關聯
    List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
    if(setmealIds != null && setmealIds.size() > 0) {
      throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
    }

//    for (Long id : ids) {
//      dishMapper.deleteById(id);
//      dishFlavorMapper.deleteByDishId(id);
//    }
//
    // 刪除菜品表中的菜品數據
    dishMapper.deleteByIds(ids);
    // 刪除菜品關聯的口味數據
    dishFlavorMapper.deleteByDishIds(ids);
  }
  /**
   * 根據id查詢菜品口味數據
   * @param id
   * @return
   */
  @Override
  public DishVO getByIdWithFlavor(Long id) {
    // 根據id查詢菜品數據
    Dish dish = dishMapper.getById(id);
    // 根據菜品id查詢口味數據
    List<DishFlavor> dishFlavors = dishMapper.getByDishId(id);
    // 將查詢到的數據封裝到VO
    DishVO dishVO = new DishVO();
    BeanUtils.copyProperties(dish, dishVO);
    dishVO.setFlavors(dishFlavors);
    return dishVO;
  }

  /**
   * 根據id修改菜品信息和口味信息
   * @param dishDTO
   */
  @Override
  public void updateWithFlavor(DishDTO dishDTO) {
    Dish dish = new Dish();
    BeanUtils.copyProperties(dishDTO, dish);
    // 修改菜品基本信息
    dishMapper.update(dish);
    // 刪除原有的口味信息
    dishFlavorMapper.deleteByDishId(dishDTO.getId());
    //重新插入口味數據
    List<DishFlavor> flavors = dishDTO.getFlavors();
    if(flavors != null && flavors.size() > 0) {
      flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
      dishFlavorMapper.insertBatch(flavors);
    }
  }
}
