package com.litchi.bbs.entity;

/**
 * 封装分页属性
 *
 * @author cuiwj
 * @date 2020/3/9
 */
public class Page {

    private int current = 1;  // 当前页
    private int limit = 10;  // 每页显示记录数
    private int rows;  // 总记录数
    private String path; // 查询路径(用于复用分页链接)

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit < 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows > 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return 当前页起始记录行
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * @return 总页数
     */
    public int getTotal() {
        return rows % limit == 0 ? rows / limit : rows / limit + 1;
    }

    /**
     * @return 起始页码
     */
    public int getFrom() {
        int from = current - 2;
        return Math.max(from, 1);
    }

    /**
     * @return 结束页码
     */
    public int getTo() {
        int to = current + 2;
        return Math.min(to, getTotal());
    }
}
