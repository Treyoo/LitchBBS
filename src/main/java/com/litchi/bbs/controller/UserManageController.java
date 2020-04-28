package com.litchi.bbs.controller;

import com.litchi.bbs.entity.User;
import com.litchi.bbs.service.UserService;
import com.litchi.bbs.util.LitchiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(path = "/manage", method =
        {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class UserManageController {
    @Autowired
    private UserService userService;

    @GetMapping(path = {"/user"})
    public String getUsers(@RequestParam(name = "offset",defaultValue = "0") int offset,
                               @RequestParam(name = "limit",defaultValue = "10") int limit) {
        return LitchiUtil.getJSONString(userService.selectUsers(offset,limit));
    }
}
