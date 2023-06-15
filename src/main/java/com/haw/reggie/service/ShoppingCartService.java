package com.haw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haw.reggie.common.R;
import com.haw.reggie.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    public void clean();
}
