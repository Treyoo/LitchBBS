package com.litchi.bbs.controller;

import com.litchi.bbs.entity.User;
import com.litchi.bbs.service.UserService;
import com.litchi.bbs.util.ActivationStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author cuiwj
 * @date 2020/3/10
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    /**
     * 跳转到登录/注册页面
     *
     * @param model
     * @param next  登录后访问的路径
     * @return
     */
    @RequestMapping(path = {"/register_page"}, method = {RequestMethod.GET})
    public String registerPage(Model model, @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);
        return "site/register";
    }

    /**
     * 注册
     *
     * @param model
     * @param response
     * @param user
     * @param next
     * @param rememberme
     * @return
     */
    @RequestMapping(path = {"/register"}, method = {RequestMethod.POST})
    public String register(Model model, User user,
                      HttpServletResponse response,
                      @RequestParam(value = "next", required = false) String next,
                      @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme) {
        try {
            Map<String, Object> map = userService.register(user);
            if (map.isEmpty()) {
//                eventProducer.fireEvent(new Event(EventType.REGISTER)
//                        .setExt("to","443353439@qq.com")
//                        .setExt("username",username)
//                        .setExt("password",password));
                model.addAttribute("message","已经向您的邮箱发送激活邮件,请查收!");
                model.addAttribute("target","/index");
                return "site/operate-result";
            } else {
                model.addAttribute("usernameMsg", map.get("usernameMsg"));
                model.addAttribute("passwordMsg", map.get("passwordMsg"));
                model.addAttribute("emailMsg", map.get("emailMsg"));
                return "site/register";
            }
        } catch (Exception e) {
            logger.error("注册异常，" + e.getMessage());
            model.addAttribute("msg", "服务器异常");
            return "site/register";
        }

    }

    @RequestMapping(path = "/login_page")
    public String loginPage(Model model){
        return "site/login";
    }

    @RequestMapping(path = {"/activation/{userId}/{activation_code}"}, method = RequestMethod.GET)
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("activation_code") String activationCode) {
        int result = userService.activation(userId,activationCode);
        if(result == ActivationStatus.ACTIVATED){
            model.addAttribute("message","激活成功,您的账号可以正常使用了!");
            model.addAttribute("target","/login_page");
        }else if(result==ActivationStatus.ACTIVATED_REPEAT){
            model.addAttribute("message","您的账号已经激活,请勿重复激活");
            model.addAttribute("target","/");
        }else{
            model.addAttribute("message","账号激活失败！激活码错误！");
            model.addAttribute("target","/");
        }
        return "site/operate-result";
    }
}
