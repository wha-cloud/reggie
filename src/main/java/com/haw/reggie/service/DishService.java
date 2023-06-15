package com.haw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haw.reggie.dto.DishDto;
import com.haw.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品和口味
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品和口味
    public void updateWithFlavor(DishDto dishDto);

    //删除菜品和口味
    public void deleteWithFlavor(String id);
}
