package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.websocket.WebSocketServer;
import io.lettuce.core.models.stream.PendingMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrderMapper orderMapper;
  @Autowired
  private OrderDetailMapper orderDetailMapper;
  @Autowired
  private AddressBookMapper addressBookMapper;
  @Autowired
  private ShoppingCartMapper shoppingCartMapper;
  @Autowired
  private WeChatPayUtil weChatPayUtil;
  @Autowired
  private WebSocketServer webSocketServer;
//  @Autowired
//  private UserMapper userMapper;
  /**
   * 用戶下單
   * @param ordersSubmitDTO
   * @return
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
    // 處理各種業務異常(地址為空、購物車為空)
    AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
    if(addressBook == null) {
      throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
    }

    Long userId = BaseContext.getCurrentId();
    ShoppingCart shoppingCart = new ShoppingCart();
    shoppingCart.setUserId(userId);
    List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

    if(shoppingCartList == null || shoppingCartList.size() == 0) {
      throw new AddressBookBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
    }
    // 向訂單插入一條數據
    Orders orders = new Orders();
    BeanUtils.copyProperties(ordersSubmitDTO, orders);
    orders.setOrderTime(LocalDateTime.now());
    orders.setPayStatus(Orders.UN_PAID);
    orders.setStatus(Orders.PENDING_PAYMENT);
    orders.setNumber(String.valueOf(System.currentTimeMillis()));
    orders.setPhone(addressBook.getPhone());
    orders.setConsignee(addressBook.getConsignee());
    orders.setId(userId);

    orderMapper.insert(orders);
    // 向訂單明細插入n條數據
    ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
    for (ShoppingCart cart : shoppingCartList) {
      // 設置訂單
      OrderDetail orderDetail = new OrderDetail();
      BeanUtils.copyProperties(cart, orderDetail);
      // 設置當前訂單明細關聯的訂單id
      orderDetail.setOrderId(orders.getId());
      orderDetailList.add(orderDetail);
    }

    orderDetailMapper.insertBatch(orderDetailList);
    // 清空當前用戶的購物車數據
    shoppingCartMapper.deleteByUserId(userId);
    // 封裝VO返回結果
    OrderSubmitVO ordersubmitVO = OrderSubmitVO.builder()
            .id(orders.getId())
            .orderTime(orders.getOrderTime())
            .orderNumber(orders.getNumber())
            .orderAmount(orders.getAmount())
            .build();
    return ordersubmitVO;
  }

  /**
   * 订单支付
   *
   * @param ordersPaymentDTO
   * @return
   */
  public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
    // 当前登录用户id
    Long userId = BaseContext.getCurrentId();
//    User user = userMapper.getById(userId);
    User user = new User(); // 為了先解決報錯，而先這樣寫
    //调用微信支付接口，生成预支付交易单
    JSONObject jsonObject = weChatPayUtil.pay(
            ordersPaymentDTO.getOrderNumber(), //商户订单号
            new BigDecimal(0.01), //支付金额，单位 元
            "苍穹外卖订单", //商品描述
            user.getOpenid() //微信用户的openid
    );

    if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
      throw new OrderBusinessException("该订单已支付");
    }

    OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
    vo.setPackageStr(jsonObject.getString("package"));

    return vo;
  }

  /**
   * 支付成功，修改订单状态
   *
   * @param outTradeNo
   */
  public void paySuccess(String outTradeNo) {

    // 根据订单号查询订单
    Orders ordersDB = orderMapper.getByNumber(outTradeNo);

    // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
    Orders orders = Orders.builder()
            .id(ordersDB.getId())
            .status(Orders.TO_BE_CONFIRMED)
            .payStatus(Orders.PAID)
            .checkoutTime(LocalDateTime.now())
            .build();

    orderMapper.update(orders);

    // 通過websocket向客戶端瀏覽器推送消息
    HashMap map = new HashMap();
    map.put("type", 1);
    map.put("orderId", ordersDB.getId());
    map.put("content", "訂單號: "+outTradeNo);
    String json = JSON.toJSONString(map);
    webSocketServer.sendToAllClient(json);
  }

  /**
   * 客戶催單
   * @param id
   */
  @Override
  public void reminder(Long id) {
//    Orders ordersDB = orderMapper.getById(id);
//
//    if(ordersDB == null) {
//      throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
//    }
//    Map map = new HashMap();
//    map.put("type", 2);
//    map.put("orderId", id);
//    map.put("content", "訂單號: " + ordersDB.getNumber());
//    String json = JSON.toJSONString(map);
//    webSocketServer.sendToAllClient(json);
  }
}
