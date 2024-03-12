package com.nowcoder.community.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/21  16:05
 * @description : 面向切面編程的测试
 **/

//@Component
//@Aspect
public class AlphaAspect {

    /*声明切点
    第一个*表示任意类型的返回值，中间的最后是到方法，因为spring aop只能到方法级别，括号中的..表示任意返回值
     */
    @Pointcut("execution(* com.nowcoder.community.service.joint.*.*(..))")
    public void pointcut(){}

    @Before(value = "pointcut()")
    public void before(){
        System.out.println("before");
    }

    @After(value = "pointcut()")
    public void after(){
        System.out.println("after");
    }

    @AfterReturning(value = "pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    @AfterThrowing(value = "pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    /*在切点前后置入
    joinPoint指切点，返回值为切点方法的返回值
    调用joinPoint.proceed()方法就是调用切点的方法
    然后在这个调用前的逻辑就是切点前的逻辑
    这个调用之后的逻辑就是切点后的逻辑
    之后自己返回就行了
     */
    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("around before");
        Object result = joinPoint.proceed();
        System.out.println("around after");
        return result;
    }
}
