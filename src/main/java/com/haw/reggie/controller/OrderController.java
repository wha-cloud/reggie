package com.haw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haw.reggie.common.BaseContext;
import com.haw.reggie.common.R;
import com.haw.reggie.dto.OrderDto;
import com.haw.reggie.entity.*;
import com.haw.reggie.service.OrderDetailService;
import com.haw.reggie.service.OrderService;
import com.haw.reggie.service.ShoppingCartService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("提交成功");
    }

    /**
     * 订单查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page,int pageSize){
        //构造分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDto> page1 = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        Long userId = BaseContext.getCurrentId();
        queryWrapper.eq(Orders::getUserId,userId);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        orderService.page(pageInfo,queryWrapper);

        List<Orders> list = pageInfo.getRecords();
        List<OrderDto> orderDtoList = new ArrayList<>();
        for (Orders orders : list) {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(orders,orderDto);
            List<OrderDetail> orderDetailList = orderService.getOrderDetailListByOrderId(orders.getId());
            orderDto.setOrderDetails(orderDetailList);
            orderDtoList.add(orderDto);
        }
        BeanUtils.copyProperties(pageInfo,page1,"records");
        page1.setRecords(orderDtoList);
        return R.success(page1);
    }

    /**
     * 管理端订单查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page1(int page,int pageSize){
        //构造分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 派送,完成订单
     * @return
     */
    @PutMapping
    public R<String> send(@RequestBody Orders orders){
        orderService.updateById(orders);
        return R.success("派送成功");
    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,orders.getId());
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        shoppingCartService.clean();
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCartList.add(shoppingCart);
        }
        shoppingCartService.saveBatch(shoppingCartList);
        return R.success("操作成功");
    }
}
