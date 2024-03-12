package com.nowcoder.community;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/23  16:36
 * @description :
 **/
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringTest {
    @BeforeTestClass
    public void beforeTestClass(){
        System.out.println("beforeTestClass");
    }

    @AfterTestClass
    public void afterTestClass(){
        System.out.println("afterTestClass");
    }

    @BeforeTestMethod
    public void before(){
        System.out.println("beforeTestMethod");
    }

    @AfterTestMethod
    public void after(){
        System.out.println("afterTestMethod");
    }

    @Test
    public void test1(){
        System.out.println("test1");
    }

    @Test
    public void test2(){
        System.out.println("test2");
    }
}
