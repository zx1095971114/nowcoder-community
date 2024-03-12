package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaDaoImpl2")
public class AlphaDaoImpl2 implements AlphaDao{
    @Override
    public String alphaDao() {
        System.out.println("alphaDao");
        return "AlphaDao2";
    }
}
