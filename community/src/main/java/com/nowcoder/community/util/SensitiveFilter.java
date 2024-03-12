package com.nowcoder.community.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/11  20:46
 * @description :敏感词过滤器，使用前缀树来实现
 **/
@Component
public class SensitiveFilter {
    private TrieNode root = new TrieNode();
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACE_WORDS = "***";

    //创建好实例以后要初始化前缀树
    @PostConstruct
    private void init(){
        try (
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String line = null;
            while ((line = reader.readLine()) != null){
                TrieNode temp = root;
                for(int i = 0; i < line.length(); i++){
                    char c = line.charAt(i);
                    temp.addChild(c);
                    temp = temp.getChild(c);
                    if(i == line.length() - 1){
                        temp.setSensitiveEnd(true);
                    }
                }
            }
        }catch (IOException e){
            logger.error("读取敏感词文件失败" + e.getMessage());
        }

    }

    /**
     * @Author Zhou Xiang
     * @Description 过滤敏感词方法
     * @Date 2023/12/11 21:53
     * @param origin 待过滤的字符串
     * @return java.lang.String 过滤后的字符串
     **/
    public String filterSensitiveWords(String origin){
        if(StringUtils.isBlank(origin)){
            return null;
        }

        //过滤时指向树的指针
        TrieNode treePoint = root;
        //过滤时指向正在过滤的字符的开始位置
        int begin = 0;
        //过滤时指向接正在过滤的字符的结束位置
        int end = 0;
        //begin,end所指向的位置都是待处理的字符所在的位置

        StringBuilder result = new StringBuilder();
        while (begin < origin.length()){
            //这时，end已经超出范围，说明begin此时的位置的字符不可能构成敏感词，
            // (因为fabce和abc都是敏感词时，fabc处于末尾，这时只能确定f构不成敏感词，但a或b或c开头可能构成敏感词)
            if(end >= origin.length()){
                result.append(origin.charAt(begin));
                begin++;
                end = begin;
                treePoint = root;
                continue;
            }

            char cur = origin.charAt(end);
            //处理特殊字符
            if(isSpecialCharacter(cur)){
                if(begin == end){
                    //说明此时是判断过程的第一个字符,此时treePoint必定指向root
                    result.append(cur);
                    begin++;
                    end = begin;
                }else {
                    end++;
                }
                continue;
            }

            TrieNode next = treePoint.getChild(cur);
            if(next == null){
                result.append(origin.charAt(begin));
                begin++;
                end = begin;
                treePoint = root;
            }else {
                if(next.isSensitiveEnd()){
                    result.append(REPLACE_WORDS);
                    end++;
                    begin = end;
                    treePoint = root;
                }else {
                    end++;
                    treePoint = next;
                }
            }
        }

        return result.toString();
    }


    //判断c是不是特殊字符
    private boolean isSpecialCharacter(char c){
        //isAsciiAlphanumeric()判断是不是字符和数字，而东亚字符集的范围是[0x2E80, 0x9FFF]
        return !CharUtils.isAsciiAlphanumeric(c) && (c > 0x9FFF || c < 0x2E80);
    }

    //前缀树结点
    private class TrieNode{
        //是否是敏感词的最后一个字符
        private boolean isSensitiveEnd = false;

        //记录下级结点的字符和所有下级结点，
        //因为前缀树的特性，决定了同一父节点下，每个子节点的字符都是不同的，所以可以用map来记录而不是搞一个char和一个List<Character>
        private Map<Character, TrieNode> children = new HashMap<>();

        public boolean isSensitiveEnd() {
            return isSensitiveEnd;
        }

        public void setSensitiveEnd(boolean sensitiveEnd) {
            isSensitiveEnd = sensitiveEnd;
        }

        //添加一个孩子，默认这个孩子是!isSensitiveEnd()
        public void addChild(char c){
            TrieNode child = children.getOrDefault(c, new TrieNode());
            children.put(c, child);
        }

        //根据字符查孩子
        public TrieNode getChild(char c){
            return children.get(c);
        }
    }
}
