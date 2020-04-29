package com.litchi.bbs.controller;

import com.litchi.bbs.entity.User;
import com.litchi.bbs.service.UserService;
import com.litchi.bbs.util.LitchiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "/manage", method =
        {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class UserManageController {
    @Autowired
    private UserService userService;

    @GetMapping(path = {"/user"})
    public String getUsers(@RequestParam(name = "offset",defaultValue = "0") int offset,
                               @RequestParam(name = "limit",defaultValue = "5") int limit) {
        Map<String,Object> map = new HashMap<>();
        map.put("users", userService.selectUsers(offset,limit));
        map.put("total", userService.getUserRows());
        return LitchiUtil.getJSONString(map);
    }
}
