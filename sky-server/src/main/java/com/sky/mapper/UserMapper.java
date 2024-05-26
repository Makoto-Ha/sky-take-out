package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper {
  /**
   * 根據動態條件統計用戶數量
   * @param map
   * @return
   */
  Integer countByMap(Map map);
}
