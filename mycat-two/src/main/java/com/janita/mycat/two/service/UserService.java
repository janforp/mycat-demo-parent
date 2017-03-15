package com.janita.mycat.two.service;

import com.janita.mycat.two.bean.User;
import com.janita.mycat.two.dao.UserDAO;
import com.janita.mycat.two.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Janita on 2017/3/15
 */
@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public User createUser(User user){
        user.setId(MyUtils.getRandomId());
        userDAO.insert(user);
        return user;
    }

    public User modifyUser(User user){
        userDAO.updateByPrimaryKeySelective(user);
        return user;
    }


    public User getUserById(String id) {
        return userDAO.selectByPrimaryKey(id);
    }
}
