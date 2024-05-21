package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定義切面，實現公共字段自動填充處理邏輯
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
  @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
  public void autoFillPointCut() {}

  @Before("autoFillPointCut()")
  public void autofill(JoinPoint joinPoint) {
    log.info("開始進行公共字段自動填充");

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
    OperationType operationType = autoFill.value();
    Object[] args = joinPoint.getArgs();
    if(args == null || args.length == 0) {
      return;
    }

    Object entity = args[0];

    // 準備賦值的數據
    LocalDateTime now = LocalDateTime.now();
    Long currentId = BaseContext.getCurrentId();

    // 根據當前不同的操作類型，為對應的屬性通過反射來賦值
    if(operationType == OperationType.INSERT) {
      try {
        Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
        Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
        Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
        setCreateTime.invoke(entity, now);
        setUpdateTime.invoke(entity, now);
        setCreateUser.invoke(entity, currentId);
        setUpdateUser.invoke(entity, currentId);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }else if(operationType == OperationType.UPDATE) {
      try {
        Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

        setUpdateTime.invoke(entity, now);
        setUpdateUser.invoke(entity, currentId);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
