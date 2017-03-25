package com.tian.singlesign.service;


import com.tian.singlesign.dao.entity.User;

/**
 * Created by tian on 2016/10/12.
 */
public interface IUserService {
    /**
     * 根据ID查询用户
     * @param id
     * @return
     */
    User queryUserById(Long id);

    /**
     * 根据用户名和密码查询用户
     * @param userName
     * @param password
     * @return
     */
    User queryUserByUserNameAndPassword(String userName, String password);

    /**
     * 根据手机号查询用户信息
     * @param mobile
     * @return
     */
    User queryUserByMobile(String mobile);
}
