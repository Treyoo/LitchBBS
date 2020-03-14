package com.litchi.bbs.controller;

/**
 * @author cuiwj
 * @date 2020/3/13
 */

import com.litchi.bbs.BbsApplication;
import com.litchi.bbs.util.LitchiUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BbsApplication.class)

//配置事务的回滚,对数据库的增删改都会回滚,便于测试用例的循环利用
@Transactional(transactionManager = "transactionManager")
@Rollback(value = true)
@WebAppConfiguration
public class DiscussPostControllerTests  {
    //记得配置log4j.properties ,的命令行输出水平是debug
    private Logger logger= LoggerFactory.getLogger(DiscussPostControllerTests.class);

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Before()  //这个方法在每个方法执行之前都会执行一遍
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();  //初始化MockMvc对象
    }

    @Test
    public void testRequest() throws Exception {
        String responseString = mockMvc.perform(
                get("/alpha")    //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)  //数据的格式
                .param("pcode","root")         //添加参数
        ).andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println();
    }


    @Test
    public void testAddDiscuss() throws Exception {
        String responseString = mockMvc.perform(post("/discuss/add")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title","Test add discuss!")
                .param("content", LitchiUtil.genRandomString())
                .cookie(new Cookie("token","eaa4ff3a295d44a8b1a749c9851915c6")))
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();
        System.out.println("服务器返回:" + responseString);
    }
}