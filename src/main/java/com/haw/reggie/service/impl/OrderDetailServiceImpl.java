package com.haw.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haw.reggie.entity.OrderDetail;
import com.haw.reggie.mapper.OrdeDetailMapper;
import com.haw.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrdeDetailMapper,OrderDetail> implements OrderDetailService {
}
