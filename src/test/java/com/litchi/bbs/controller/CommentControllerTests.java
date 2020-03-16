package com.litchi.bbs.controller;

import com.litchi.bbs.BbsApplication;
import com.litchi.bbs.util.LitchiUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author cuiwj
 * @date 2020/3/14
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BbsApplication.class)
@WebAppConfiguration
@Transactional(transactionManager = "transactionManager")
@Rollback
public class CommentControllerTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testAddComment() throws Exception {
        String result = mockMvc.perform(post("/comment/add")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(new Cookie("token","2ada6e712cea49fd9ae865621f480577"))
                .param("postId", "293")
                .param("content", "peace" + LitchiUtil.genRandomString())
        ).andExpect(status().is3xxRedirection())
                .andDo(print()).andReturn()
                .getResponse().toString();
        System.out.println("Server return:" + result);

    }

}
