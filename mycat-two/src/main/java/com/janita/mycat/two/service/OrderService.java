package com.janita.mycat.two.service;

import com.janita.mycat.two.bean.Orders;
import com.janita.mycat.two.dao.OrdersDAO;
import com.janita.mycat.two.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Janita on 2017/3/15 0015.
 */
@Service
public class OrderService {

    @Autowired
    private OrdersDAO ordersDAO;
    @Autowired
    private DetailService detailService;

    public Orders createOrder(Orders order){
        order.setOrderId(MyUtils.getRandomId());
        ordersDAO.insert(order);
        return order;
    }
}
