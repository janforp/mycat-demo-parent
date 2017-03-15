package com.janita.mycat.two.service;

import com.janita.mycat.two.bean.Food;
import com.janita.mycat.two.dao.FoodDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Janita on 2017/3/15
 */
@Service
public class FoodService {

    @Autowired
    private FoodDAO foodDAO;

    public Food createFood(Food food){
        foodDAO.insert(food);
        return food;
    }

    public Food modifyFood(Food food){
        foodDAO.updateByPrimaryKey(food);
        return food;
    }

    public Food findFoodById(Long foodId){
        return foodDAO.selectByPrimaryKey(foodId);
    }

    public Food removeFoodById(Long foodId){
        foodDAO.deleteByPrimaryKey(foodId);
        return foodDAO.selectByPrimaryKey(foodId);
    }
}
