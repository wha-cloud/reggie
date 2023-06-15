package com.haw.reggie.dto;

import com.haw.reggie.entity.Setmeal;
import com.haw.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
