package com.haw.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haw.reggie.dto.DishDto;
import com.haw.reggie.entity.Dish;
import com.haw.reggie.entity.DishFlavor;
import com.haw.reggie.mapper.DishMapper;
import com.haw.reggie.service.DishFlavorService;
import com.haw.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Value("${reggie.path}")
    private String basePath;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存口味
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到dish表格
        this.save(dishDto);
        Long id = dishDto.getId();//  菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
        }
        //保存口味到dish_flavor表格
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 查询菜品和口味信息
     * @param id
     * @return
     */
    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto ;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
//        Dish dish = new Dish();
//        BeanUtils.copyProperties(dishDto,dish);
        this.updateById(dishDto);
        List<DishFlavor> list = dishDto.getFlavors();
        //先清理之前的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        for (DishFlavor dishFlavor : list) {
            dishFlavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(list);
    }

    @Override
    @Transactional
    public void deleteWithFlavor(String id) {
        //删除图像
        Dish dish = this.getById(id);
        String image = dish.getImage();
        String path = basePath + image;
        File file = new File(path);
        if(file.exists()){
            boolean f = file.delete();
        }
        this.removeById(id);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(queryWrapper);

    }
}
