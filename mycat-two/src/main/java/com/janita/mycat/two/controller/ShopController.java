package com.janita.mycat.two.controller;

import com.janita.mycat.two.bean.Shop;
import com.janita.mycat.two.constant.ResultDto;
import com.janita.mycat.two.service.ShopService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Janita on 2017/3/15
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @PostMapping
    @ApiOperation(value = "新建shop")
    public Shop createShop(@RequestBody Shop shop){
        return shopService.createShop(shop);
    }

    @PutMapping
    @ApiOperation(value = "修改shop")
    public Shop modifyShop(@RequestBody Shop shop){
        return shopService.modifyShop(shop);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询shop")
    public Shop getShop(@PathVariable String id){
        return shopService.findShopById(id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id删除shop")
    public ResultDto removeShop(@PathVariable String id){
        Integer rows = shopService.removeShopById(id);
        return ResultDto.success("成功删除"+rows+"条",null);
    }
}
