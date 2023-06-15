package com.haw.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haw.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdeMapper extends BaseMapper<Orders> {
}
