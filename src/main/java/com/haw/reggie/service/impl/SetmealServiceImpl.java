package com.haw.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haw.reggie.common.CustomException;
import com.haw.reggie.dto.DishDto;
import com.haw.reggie.dto.SetmealDto;
import com.haw.reggie.entity.Dish;
import com.haw.reggie.entity.DishFlavor;
import com.haw.reggie.entity.Setmeal;
import com.haw.reggie.entity.SetmealDish;
import com.haw.reggie.mapper.SetmealMapper;
import com.haw.reggie.service.SetmealDishService;
import com.haw.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Value("${reggie.path}")
    private String basePath;

    @Autowired
    private SetmealDishService setmealDishService;


    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishList) {
            setmealDish.setSetmealId(setmealDto.getId());
        }

        setmealDishService.saveBatch(setmealDishList);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，停售才能删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = super.count(queryWrapper);
        if(count>0){
            throw new CustomException("套餐售卖中，不能删除");
        }
        //删除图像
        for (Long id : ids) {
            Setmeal setmeal = super.getById(id);
            String image = setmeal.getImage();
            String path = basePath + image;
            File file = new File(path);
            if(file.exists()){
                boolean f = file.delete();
            }
        }

        //如果可以删除，先删除套餐表
        super.removeByIds(ids);
        //然后删除关系表的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = super.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        super.updateById(setmealDto);
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        //先清理之前的口味
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        for (SetmealDish setmealDish : list) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(list);
    }
}
