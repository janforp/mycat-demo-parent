package com.janita.mycat.two.controller;

import com.janita.mycat.two.bean.User;
import com.janita.mycat.two.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Janita on 2017/3/15
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @ApiOperation(value = "添加用户user")
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @PutMapping
    @ApiOperation(value = "修改user")
    public User modifyUser(@RequestBody User user){
        return userService.modifyUser(user);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查用户")
    public User getUser(@PathVariable String id){

        return userService.getUserById(id);
    }
}
