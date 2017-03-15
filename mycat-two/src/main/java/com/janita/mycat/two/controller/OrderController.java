package com.janita.mycat.two.controller;

import com.janita.mycat.two.bean.Orders;
import com.janita.mycat.two.constant.ResultDto;
import com.janita.mycat.two.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Janita on 2017/3/15 0015.
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @ApiOperation(value = "添加order，生成对应的orderDetail")
    public ResultDto createOrder(@RequestBody Orders order){
        return ResultDto.success("添加成功",orderService.createOrder(order));
    }
}
