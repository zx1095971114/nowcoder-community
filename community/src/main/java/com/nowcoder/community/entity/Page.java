package com.nowcoder.community.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : Zhou Xiang
 * @date : 2023/11/30  21:07
 * @description :整合关于分页有关的信息,使用的时候path，recordsCount必须要有，currentPage,limit,distance可以设置，不设可以用默认值
 **/
public class Page {
    private static final Logger logger = LoggerFactory.getLogger(Page.class);
    //当前页面号
    private int currentPage = 1;
    //总的记录数
    private int recordsCount = 0;
    //每个页面限制的记录条数
    private int limit = 10;
    //初始页面访问路径
    private String path = "";
    //显示前distance页和后distance页
    private int distance = 2;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        if(currentPage <= 0){
            logger.info("非法的currentPage");
            return;
        }
        this.currentPage = currentPage;
    }

    public int getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(int recordsCount) {
        if(recordsCount < 0){
            logger.warn("非法的recordsCount");
            return;
        }
        this.recordsCount = recordsCount;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit < 0 || limit > 100){
            logger.info("非法的limit");
            return;
        }
        this.limit = limit;
    }

    public int getPageCount() {
        if(recordsCount < 0){
            logger.warn("记录条数为负数！");
            return 0;
        }
        if(recordsCount % limit == 0){
            return recordsCount / limit;
        }else {
            return recordsCount / limit + 1;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOffset(){
        return (currentPage - 1) * limit;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        if(distance <= 0){
            logger.warn("非法的distance");
            return;
        }
        this.distance = distance;
    }

    /**
     * @Author Zhou Xiang
     * @Description 获取起始页码
     * @Date 2023/12/1 16:38
     * @Param void
     * @return int
     **/
    public int getFrom(){
        int from = currentPage - distance;
        if(from >= 1){
            return from;
        }else {
            return 1;
        }
    }

    /**
     * @Author Zhou Xiang
     * @Description 获取终止页码
     * @Date 2023/12/1 16:43
     * @Param void
     * @return int
     **/
    public int getTo(){
        int to = currentPage + distance;
        if(to <= getPageCount()){
            return to;
        }else {
            return getPageCount();
        }
    }

    /**
     * @Author Zhou Xiang
     * @Description 获取当前页面的条目数量
     * @Date 2024/2/11 22:35
     * @Param void
     * @return int
     **/
    public int getPageRecordsCount(){
        if(currentPage < getPageCount()){
            return limit;
        }

        return recordsCount - (currentPage - 1) * limit;
    }
}
