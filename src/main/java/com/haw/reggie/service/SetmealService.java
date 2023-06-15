package com.haw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haw.reggie.dto.DishDto;
import com.haw.reggie.dto.SetmealDto;
import com.haw.reggie.entity.Setmeal;
import com.haw.reggie.entity.SetmealDish;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，用时保存菜品
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，及其菜品
     */
    public void removeWithDish(List<Long> ids);

    //根据id查询套餐和菜品
    public SetmealDto getByIdWithDish(Long id);

    //更新套餐
    public void updateWithDish(SetmealDto setmealDto);
}
