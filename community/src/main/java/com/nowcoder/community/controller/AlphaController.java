package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommonUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
//@RequestMapping(path = "/alpha")
public class AlphaController {
    private final AlphaService alphaService;

    @Autowired
    public AlphaController(AlphaService alphaService) {
        this.alphaService = alphaService;
        Stack<Integer> s;
    }

    @RequestMapping(path = "/data/alpha/security", method = RequestMethod.GET)
    @ResponseBody
    public String getSecurity(){
        return CommonUtils.getJSONString(200, "安全员方法");
    }


    public String alphaController(){
        System.out.println("controller");
        return alphaService.service();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取请求数据
        System.out.println("请求方法" + request.getMethod());
        System.out.println("路径 " + request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String key = enumeration.nextElement();
            String value = request.getHeader(key);
            System.out.println(key + ": " + value);
        }
        System.out.println("参数" + request.getParameter("secret"));

        //返回响应数据
        response.setContentType("text/html; charset = utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("<h1>牛客网</h1>");
    }

    //获取GET请求的参数的两种方式(浏览器请求数据)
   // 1.跟在?后面 如/student?currentPage=1&limit=20
    @RequestMapping(path = "/student", method = RequestMethod.GET)
    @ResponseBody
    public String firstGet(
            //注意，这里的RequestParam注解可以不加，默认的name是命名的参数的名称
            @RequestParam(name = "currentPage", required = false, defaultValue = "1") int currentPage,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit
    ){
        System.out.println("currentPage: " + currentPage);
        System.out.println("limit: " + limit);
        return "Students";
    }

    @RequestMapping(path = "/test1", method = RequestMethod.GET)
    @ResponseBody
    public String test1(A a){
        int b = 0;
        return "test1";
    }

    //2.在路径中
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public  String secondGet(
            @PathVariable(name = "id", required = false) int id
    ){
        System.out.println("id: " + id);
        return "a student";
    }

    //POST请求处理
    @RequestMapping(path = "/submit", method = RequestMethod.POST)
    public String postSubmit(String name, int id){
        System.out.println("name: " + name);
        System.out.println("id: " + id);
        return "redirect:/index";
    }

    //返回动态网页
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张三");
        mav.addObject("age", 10);
        mav.setViewName("/demo/view");
        return mav;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name", "天津大学");
        model.addAttribute("age", 120);
        return "demo/view";
    }

    //响应JSON数据(异步请求，如注册时填写昵称，不刷新网页但会返回昵称是否可用)
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("name", "Peter");
        emp1.put("age", 10);
        emp1.put("salary", 10000.0);

        return emp1;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> emps = new ArrayList<>();

        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("name", "Peter");
        emp1.put("age", 10);
        emp1.put("salary", 10000.0);
        emps.add(emp1);

        emp1 = new HashMap<>();
        emp1.put("name", "Frank");
        emp1.put("age", 12);
        emp1.put("salary", 15000.0);
        emps.add(emp1);

        return emps;
    }

    @RequestMapping(path = "/A", method = RequestMethod.GET)
    @ResponseBody
    public A getA(){
        A a = new A();

        return a;
    }

    @RequestMapping(path = "/static", method = RequestMethod.GET)
    public String getStatic(){
        return "/demo/view";
    }

    @RequestMapping(path = "/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("myCookie0", CommonUtils.generateUUID());
        cookie.setPath("community/alpha");
        //单位是s
        cookie.setMaxAge(60 * 10);
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("myCookie0") String cookie){
        System.out.println("cookie: " + cookie);
        return "get cookie";
    }

    @RequestMapping(path = "/session/set")
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id", "mySession");
        session.setAttribute("A", new A());
        return "set Session";
    }

    @RequestMapping(path = "/session/get")
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println("id: " + session.getAttribute("id"));
        System.out.println("A: " + session.getAttribute("A"));
        return "get Session";
    }

    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String getAjax(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return CommonUtils.getJSONString(200, "操作成功");
    }
}

class A{
    public int a = 3;
    public int b = 4;

    public void hi(){
        System.out.println("hi");
    }

    @Override
    public String toString() {
        return "A{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
