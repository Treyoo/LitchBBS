package com.litchi.bbs.controller;

import com.litchi.bbs.entity.DiscussPost;
import com.litchi.bbs.entity.EntityType;
import com.litchi.bbs.entity.Page;
import com.litchi.bbs.service.ElasticsearchService;
import com.litchi.bbs.service.LikeService;
import com.litchi.bbs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cuiwj
 * @date 2020/4/6
 */
@Controller
public class SearchController {
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    //search?q=
    @GetMapping(path = "/search")
    public String search(Model model, Page page, @RequestParam("q") String keyword) {
        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult = elasticsearchService
                .searchDiscuss(keyword, page.getCurrent() - 1, page.getLimit());
        List<Map<String, Object>> vos = new ArrayList<>();
        if (searchResult != null) {
            //分页信息
            page.setPath("/search?q=" + keyword);
            page.setRows(searchResult.getTotalPages());
            //聚合数据
            for (DiscussPost post : searchResult) {
                Map<String, Object> vo = new HashMap<>(4);
                vo.put("post", post);
                vo.put("user", userService.selectUserById(post.getUserId()));
                vo.put("likeCount", likeService.getLikeCount(EntityType.DISCUSS_POST, post.getId()));
                vos.add(vo);
            }
        }
        model.addAttribute("discussPosts", vos);
        model.addAttribute("keyword", keyword);
        return "site/search";
    }
}

