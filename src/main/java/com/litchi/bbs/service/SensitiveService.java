package com.litchi.bbs.service;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * author:CuiWJ
 * date:2018/12/7
 */
@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
    private static final String DEFAULT_REPLACEMENT = "***";

    private class TrieNode {
        private boolean isEnd = false;
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public void addSubNode(Character ch, TrieNode node) {
            subNodes.put(ch, node);
        }

        public TrieNode getSubNode(Character ch) {
            return subNodes.get(ch);
        }

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean isEnd) {
            this.isEnd = isEnd;
        }
    }

    private TrieNode rootNode;

    /**
     * 判断一个字符是非ascii字符或者非东亚字符
     *
     * @param c
     * @return 不是ascii字符或者东亚字符返回true, 否则返回false
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    /**
     * 向字典树中添加一个敏感词
     * @param lineText
     */
    private void addWord(String lineText) {
        TrieNode curNode = rootNode;
        for (int i = 0; i < lineText.length(); i++) {
            Character ch = lineText.charAt(i);
            //跳过空格和一些火星文
            if (isSymbol(ch)) {
                continue;
            }
            TrieNode subNode = curNode.getSubNode(ch);
            if (subNode == null) {
                subNode = new TrieNode();
                curNode.addSubNode(ch, subNode);
            }
            curNode = subNode;
            //最后一个节点加结束标志
            if (i == lineText.length() - 1) {
                curNode.setEnd(true);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();
        BufferedReader bufr = null;
        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            bufr = new BufferedReader(new InputStreamReader(is));
            String lineText;
            while ((lineText = bufr.readLine()) != null) {
                addWord(lineText.trim());
            }
        } catch (Exception e) {
            logger.error("读取敏感词配置文件失败" + e.getMessage());
        } finally {
            if (bufr != null) {
                bufr.close();
            }
        }
    }

    /**
     * 过滤文本中的敏感词
     * @param text
     * @return 过滤后的文本
     */
    public String filter(String text) {
        int begin = 0, position = 0;
        TrieNode curNode = rootNode;
        StringBuilder res = new StringBuilder();
        while (position < text.length()) {
            Character ch = text.charAt(position);
            if (isSymbol(ch)) {
                if (curNode == rootNode) {
                    res.append(ch);
                    ++begin;
                }
                ++position;
                continue;
            }
            TrieNode subNode = curNode.getSubNode(ch);
            if (subNode == null) {//begin处的字符可信
                res.append(ch);
                position = ++begin;
                curNode = rootNode;
            } else if (subNode.isEnd()) {//begin到position的字符是敏感词
                res.append(DEFAULT_REPLACEMENT);
                begin = ++position;
                curNode = rootNode;
            } else {//当前字符依然是可疑的，继续向后检测
                ++position;
                curNode = subNode;
            }
        }

        res.append(text.substring(begin));
        return res.toString();
    }

}
