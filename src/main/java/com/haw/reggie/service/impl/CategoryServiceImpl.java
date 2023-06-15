package com.haw.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haw.reggie.common.CustomException;
import com.haw.reggie.entity.Category;
import com.haw.reggie.entity.Dish;
import com.haw.reggie.entity.Setmeal;
import com.haw.reggie.mapper.CategoryMapper;
import com.haw.reggie.service.CategoryService;
import com.haw.reggie.service.DishService;
import com.haw.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联了菜品
        if(count>0){
            throw new CustomException("当前分类下面关联了菜品，不能删除");
        }

        //查询是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count_Setmeal = setmealService.count(setmealLambdaQueryWrapper);
        if(count_Setmeal>0){
            throw new CustomException("当前分类下面关联了套餐，不能删除");
        }

        //正常删除
        super.removeById(id);
    }
}
