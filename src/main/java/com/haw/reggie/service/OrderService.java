package com.haw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haw.reggie.entity.OrderDetail;
import com.haw.reggie.entity.Orders;

import java.util.List;

public interface OrderService extends IService<Orders> {

    public void submit(Orders orders);

    public List<OrderDetail> getOrderDetailListByOrderId(Long orderId);
}
