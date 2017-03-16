package com.janita.mycat.two.service;

import com.janita.mycat.two.bean.OrderDetail;
import com.janita.mycat.two.bean.Orders;
import com.janita.mycat.two.dao.OrderDetailDAO;
import com.janita.mycat.two.dao.OrdersDAO;
import com.janita.mycat.two.exception.NotFoundException;
import com.janita.mycat.two.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Janita on 2017/3/16
 */
@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailDAO orderDetailDAO;
    @Autowired
    private OrdersDAO ordersDAO;

    public OrderDetail createOrderDetail(OrderDetail orderDetail){
        String orderId = orderDetail.getOrderId();
        Orders order = ordersDAO.selectByPrimaryKey(orderId);
        if (order == null){
            throw new NotFoundException("订单没找到");
        }
        orderDetail.setDetailId(MyUtils.getRandomId());
        orderDetailDAO.insert(orderDetail);
        return orderDetail;
    }
}
