package com.yupi.usercenter.service;

import com.yupi.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
/**
* 用户服务测试
*
*@author WuliGao
*/

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUserName("WuliGaogaoName");
        user.setUserAccount("WuliGaogao");
        user.setAvatarUrl("https://cdn.jsdelivr.net/gh/imgPicture/cloudimg/img/dog.jpeg");
        user.setGender(0);
        user.setUserPassword("12345678");
        user.setPhone("13929738030");
        user.setEmail("1554437607@qq.com");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertEquals(true,result);
    }


    @Test
    void userRegister() {
        String userAccount = "testGao";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        System.out.println(result);
        Assertions.assertTrue(result > 0);
    }
}