package com.haw.reggie.dto;

import com.haw.reggie.entity.OrderDetail;
import com.haw.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}
