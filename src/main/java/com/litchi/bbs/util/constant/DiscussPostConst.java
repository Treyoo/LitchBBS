package com.litchi.bbs.util.constant;

/**
 * @author cuiwj
 * @date 2020/4/8
 */
public interface DiscussPostConst {
    /**
     * 普通类型
     */
    int TYPE_NORMAL = 0;
    /**
     * 置顶类型
     */
    int TYPE_TOP = 1;

    /**
     * 普通状态
     */
    int STATUS_NORMAL = 0;
    /**
     * 精华状态
     */
    int STATUS_HIGHLIGHT = 1;
    /**
     * 删除状态
     */
    int STATUS_DELETED = 2;

    /**
     * 帖子列表按发表时间倒序排序
     */
    int ORDER_BY_CREATE_TIME = 0;

    /**
     * 帖子列表按帖子分数倒序排序
     */
    int ORDER_BY_SCORE = 1;
}
