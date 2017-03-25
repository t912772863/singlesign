package com.tian.singlesign.service;

import com.tian.singlesign.dao.entity.User;
import com.tian.singlesign.dao.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by tian on 2016/10/12.
 */
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    public User queryUserById(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    public User queryUserByUserNameAndPassword(String userName, String password) {
        return userMapper.queryByUserNameAndPassword(userName,password);
    }

    public User queryUserByMobile(String mobile) {
        return userMapper.queryByMobile(mobile);
    }
}
