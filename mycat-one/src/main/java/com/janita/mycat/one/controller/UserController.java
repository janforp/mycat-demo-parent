package com.janita.mycat.one.controller;

import com.janita.mycat.one.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Janita on 2017/3/15
 */
@RestController
@RequestMapping("/user")
public class UserController {
//
//    @Autowired
//    private UserService userService ;
//
//    @PostMapping
//    @ApiOperation(value = "新增user")
//    public User createUser(@RequestBody User user){
//        return userService.createUser(user);
//    }
}
