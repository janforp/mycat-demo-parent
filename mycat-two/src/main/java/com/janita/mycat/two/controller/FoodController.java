package com.janita.mycat.two.controller;

import com.janita.mycat.two.bean.Food;
import com.janita.mycat.two.constant.ResultDto;
import com.janita.mycat.two.service.FoodService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Janita on 2017/3/15
 */
@RestController
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @PostMapping
    @ApiOperation(value = "创建food")
    public ResultDto createFood(@RequestBody Food food){
        return ResultDto.success("成功",foodService.createFood(food));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改food")
    public ResultDto modifyFood(@RequestBody Food food){
        return ResultDto.success("修改成功",foodService.modifyFood(food));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id删除food")
    public ResultDto removeFood(@PathVariable Long foodId){
        return ResultDto.success("删除成功",foodService.removeFoodById(foodId));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询food")
    public ResultDto getFood(@PathVariable Long foodId){
        return ResultDto.success(null,foodService.findFoodById(foodId));
    }
}
