package com.litchi.bbs.service;

import com.litchi.bbs.dao.UserDAO;
import com.litchi.bbs.entity.LoginToken;
import com.litchi.bbs.entity.User;
import com.litchi.bbs.util.constant.ActivationStatus;
import com.litchi.bbs.util.JedisAdapter;
import com.litchi.bbs.util.LitchiUtil;
import com.litchi.bbs.util.RedisKeyUtil;
import com.litchi.bbs.util.constant.LoginTokenStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author cuiwj
 * @date 2020/3/8
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private MailService mailService;
    @Autowired
    private TemplateEngine templateEngine;
    /*@Autowired
    private LoginTokenDAO loginTokenDAO;*/
    @Autowired
    JedisAdapter jedisAdapter;
    @Value("${bbs.path.domain}")
    private String domain;

    public User selectUserById(int userId) {
        User user = getUserCache(userId);//从redis缓存获取提升效率
        if (user == null) {
            user = userDAO.selectById(userId);
            initUserCache(user);
        }
        return user;
    }


    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>(2);
        //检测用户名密码合法性
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }

        if (userDAO.selectByName(user.getUsername()) != null) {
            map.put("usernameMsg", "用户名已存在");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (userDAO.selectByEmail(user.getEmail()) != null) {
            map.put("emailMsg", "该邮箱已经被注册");
            return map;
        }
        //新增用户
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        user.setSalt(LitchiUtil.genRandomString().substring(0, 5));
        user.setPassword(LitchiUtil.MD5(user.getPassword() + user.getSalt()));
        user.setActivationCode(LitchiUtil.genRandomString());
        user.setCreateTime(new Date());
        user.setStatus(ActivationStatus.INACTIVATED);
        user.setType(0);
        userDAO.addUser(user);

        // send activation email with activation link
        //e.g. http://localhost:8080/activation/userId/activation_code
        String activation_link = domain + "/activation/"
                + user.getId() + "/" + user.getActivationCode();
        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("activation_link", activation_link);
        String content = templateEngine.process("/mail/activation", context);
        mailService.send(user.getEmail(), "账号激活", content);
        return map;
    }

    public int activation(int userId, String activationCode) {
        User user = selectUserById(userId);
        if (user == null) {
            return ActivationStatus.INACTIVATED;
        }
        if (user.getStatus() == ActivationStatus.ACTIVATED) {
            return ActivationStatus.ACTIVATED_REPEAT;
        }
        if (user.getStatus() == ActivationStatus.INACTIVATED
                && user.getActivationCode().equals(activationCode)) {
            user.setStatus(ActivationStatus.ACTIVATED);
            userDAO.updateStatus(user);
            clearUserCache(userId);
            return ActivationStatus.ACTIVATED;
        }
        return ActivationStatus.INACTIVATED;
    }

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<>(2);
        //检测用户名密码合法性
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "用户名不存在");
            return map;
        }

        //校验密码
        if (!user.getPassword().equals(LitchiUtil.MD5(password + user.getSalt()))) {
            map.put("passwordMsg", "密码错误");
            return map;
        }
        //生成token返回客户端
        map.put("token", genToken(user));
        return map;
    }

    public void logout(String token) {
        /* loginTokenDAO.updateStatus(token,1);*/
        String redisKey = RedisKeyUtil.getLoginTokenKey(token);
        LoginToken loginToken = LitchiUtil.parseObject(jedisAdapter.get(redisKey), LoginToken.class);
        loginToken.setStatus(LoginTokenStatus.INVALID);
        jedisAdapter.set(redisKey, LitchiUtil.toJSONString(loginToken));
    }

    public void updateHeaderUrl(int userId, String headerUrl) {
        userDAO.updateHeaderUrl(userId, headerUrl);
        clearUserCache(userId);
    }

    /**
     * 生成token
     *
     * @param user
     * @return
     */
    private String genToken(User user) {
        LoginToken token = new LoginToken();
        token.setUserId(user.getId());
        token.setToken(LitchiUtil.genRandomString());
        token.setStatus(LoginTokenStatus.VALID);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 24 * 7);//设置24*7小时有效期
        token.setExpired(date);
        /*loginTokenDAO.addToken(token);*/
        //把token存入redis中
        String redisKey = RedisKeyUtil.getLoginTokenKey(token.getToken());
        jedisAdapter.set(redisKey, LitchiUtil.toJSONString(token));
        return token.getToken();
    }

    //缓存用户
    private void initUserCache(User user) {
        String redisKey = RedisKeyUtil.getUserKey(user.getId());
        jedisAdapter.setex(redisKey, LitchiUtil.toJSONString(user),1800);

    }

    //从缓存获取用户
    private User getUserCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return LitchiUtil.parseObject(jedisAdapter.get(redisKey), User.class);
    }

    //删除缓存
    private void clearUserCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        jedisAdapter.del(redisKey);
    }
}
