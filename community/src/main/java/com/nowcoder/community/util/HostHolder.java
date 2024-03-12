package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/6  23:07
 * @description :代替session来存储线程信息，注意要考虑不同线程间不能互相影响
 **/
@Component
public class HostHolder {
    //存储线程中的User对象
    private ThreadLocal<User> myUser = new ThreadLocal<>();
    public void setUser(User user){
        myUser.set(user);
    }
    public User getUser(){
        return myUser.get();
    }

    public void removeUser(){
        if(myUser.get() != null){
            myUser.remove();
        }
    }

}
