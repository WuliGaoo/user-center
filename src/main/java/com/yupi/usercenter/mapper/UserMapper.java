package com.yupi.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.usercenter.model.domain.User;

// Mapper接口，它继承自MyBatis-Plus的BaseMapper接口，并指定你的实体类作为泛型参数。
// 可以使用BaseMapper中定义的所有方法，包括save方法

/**
* @author Administrator
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-06-26 00:13:55
* @Entity com.yupi.usercenter.model.domain.User
*/
public interface UserMapper extends BaseMapper<User> {
    // 这里不需要定义额外的方法，因为BaseMapper已经包含了save等方法
}




