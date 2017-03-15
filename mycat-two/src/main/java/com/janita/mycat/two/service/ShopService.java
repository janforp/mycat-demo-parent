package com.janita.mycat.two.service;

import com.janita.mycat.two.bean.Shop;
import com.janita.mycat.two.dao.ShopDAO;
import com.janita.mycat.two.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Janita on 2017/3/15
 */
@Service
public class ShopService {

    @Autowired
    private ShopDAO shopDAO;

    public Shop createShop(Shop shop){
        shop.setId(MyUtils.getRandomId());
        shopDAO.insert(shop);
        return shop;
    }

    public Shop modifyShop(Shop shop) {
        shopDAO.updateByPrimaryKey(shop);
        return shop;
    }

    public Shop findShopById(String id) {
        return shopDAO.selectByPrimaryKey(id);
    }

    public Integer removeShopById(String id) {
       return shopDAO.deleteByPrimaryKey(id);
    }
}
