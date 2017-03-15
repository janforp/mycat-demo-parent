package com.janita.mycat.two.service;

import com.janita.mycat.two.bean.OrderDetail;
import com.janita.mycat.two.dao.OrderDetailDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Janita on 2017/3/15
 */
@Service
public class DetailService {

    @Autowired
    private OrderDetailDAO orderDetailDAO;

    public OrderDetail createDetail(OrderDetail detail){
        orderDetailDAO.insert(detail);
        return detail;
    }
}
