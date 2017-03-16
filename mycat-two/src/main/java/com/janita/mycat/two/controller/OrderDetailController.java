package com.janita.mycat.two.controller;

import com.janita.mycat.two.bean.OrderDetail;
import com.janita.mycat.two.constant.ResultDto;
import com.janita.mycat.two.service.OrderDetailService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Janita on 2017/3/16
 */
@RestController
@RequestMapping("/detail")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping
    @ApiOperation(value = "添加订单详情")
    public ResultDto createDetail(@RequestBody OrderDetail orderDetail){
        return ResultDto.success("成功",orderDetailService.createOrderDetail(orderDetail));
    }
}