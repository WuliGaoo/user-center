package com.yupi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.service.UserService;
import com.yupi.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yupi.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author wuliGao
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "wuliGao";

    /**
     * 用户登录态键
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1; // 返回错误码，表示输入为空
        }
        if (userAccount.length() < 4) {
            return -1; // 账户长度不足
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1; // 密码长度不足
        }
        // 检查账户是否包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(validPattern);
        Matcher matcher = pattern.matcher(userAccount);
        if (matcher.find()) {
            return -1; // 账户包含特殊字符
        }
        if (!userPassword.equals(checkPassword)) {
            return -1; // 密码不匹配
        }
        // 检查账户是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -1; // 账户已存在
        }
        // 对密码进行加密
        String encryPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryPassword);
        // 插入用户到数据库
        boolean result = save(user); // 使用MyBatis-Plus的save方法插入数据
        if (result) {
            log.info(user.getUserAccount()+":已注册");
            return user.getId(); // 假设User类有一个getId()方法返回主键ID
        } else {
            log.info(user.getUserName()+"：注册失败");
            return -1; // 插入失败
        }
    }

    /**
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request 获取请求信息：HttpServletRequest对象包含了客户端发送的所有请求信息，比如请求头（Headers）、参数（Parameters）、cookies等。尽管@RequestBody已经处理了请求体中的JSON或XML数据并将其转换为UserLoginRequest对象
     * @return
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null; // 返回错误码，表示输入为空
        }
        if (userAccount.length() < 4) {
            return null; // 账户长度不足
        }
        if (userPassword.length() < 8) {
            return null; // 密码长度不足
        }
        // 检查账户是否包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(validPattern);
        Matcher matcher = pattern.matcher(userAccount);
        if (matcher.find()) {
            return null; // 账户包含特殊字符
        }

        // 对密码进行加密
        String encryPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 从数据库中，检查账户是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login failed, userAccount don't match userPassword");
            return null;
        }

        User safetyUser = getSafetyUser(user);
        //记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        log.info(request.getSession().getId());
        log.info("user "+ user.getUserAccount() +" login Passed");
        return safetyUser;
    }

    /**
     *
     * @param originUser
     * @return 脱敏用户
     */
    @Override
    public User getSafetyUser(User originUser){
        //用户脱敏
        User safetyUser =  new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

}




