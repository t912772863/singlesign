package com.tian.singlesign.controller;

import com.sun.istack.internal.NotNull;
import com.tian.common.util.JedisUtil;
import com.tian.common.util.RandomUtil;
import com.tian.common.validation.Regular;
import com.tian.singlesign.controller.common.ResponseData;
import com.tian.singlesign.dao.entity.User;
import com.tian.singlesign.service.IUserService;
import com.tian.springmvcmybatis.remote.ITestRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 与登录相关的控制层(其它不需要做登录校验的请求也放这里,方便统一处理)
 * Created by Administrator on 2016/12/13 0013.
 */
@Controller
@RequestMapping("login")
public class LoginController extends BaseController{
    @Autowired
    private IUserService userService;
    @Autowired
    private ITestRemoteService testRemoteService;

    @RequestMapping("test")
    @ResponseBody
    public ResponseData test(){
        String s = testRemoteService.print("终于成功啦...");
        return success.setData(s);
    }

    /**
     * 检查是否登录了
     * @param request
     * @param response
     */
    @RequestMapping("check_login")
    @ResponseBody
    public ResponseData checkLogin(HttpServletRequest request, HttpServletResponse response,String url) throws Exception{
        request.getSession(true).setAttribute("url",url);
        User user = getSessionUser(request);
        if(user == null){
            // 项目配置的访问路径
            String s =request.getContextPath();
            // 当前请求的访问路径,包括了项目访问路径,但是不包括ip和端口号
            String s2 = request.getRequestURI();
            // 本次请求的完整路径,也就是浏览器地址栏中看到的.
            String s3 = request.getRequestURL().toString();

            // 处理上面几个路径,得到项目启动默认访问的路径,一般为登录页面.
            response.sendRedirect(s3.replace(s2,"")+s);
        }
        return success.setData(user);
    }

    /**
     * 用户登录接口(用户名,密码)
     * @param userName
     * @param password
     * @return
     */
    @RequestMapping("login")
    public ResponseData login(@NotNull String userName, @NotNull String password, HttpServletRequest request,HttpServletResponse response) throws Exception{
        User user = userService.queryUserByUserNameAndPassword(userName,password);
        if(user != null){
            // 用户登录成功,把当前登录用户信息放入Session中管理
            request.getSession().setAttribute("user",user);
            String token = UUID.randomUUID().toString();
            request.setAttribute("token",token);


            response.sendRedirect(request.getSession(true).getAttribute("url").toString());
//            return successData.setData(user);
        }
        return failed.setData("登录失败");
    }

    /**
     * 通过手机号,验证码登录
     * @param mobile
     * @param code
     * @return
     */
    @RequestMapping("login_code")
    @ResponseBody
    public ResponseData loginCode(@Regular("^(1[0-9]{10})$")String mobile, @NotNull String code, HttpServletRequest request){
        String c = JedisUtil.get(mobile);
        if(c != null && c.equals(code)){
            User user = userService.queryUserByMobile(mobile);
            request.getSession().setAttribute("user",user);
            return successData.setData(user);
        }
        return failed.setData("验证码无效");
    }

    /**
     * 模拟获取验证码
     * @return
     */
    @RequestMapping("get_check_code")
    @ResponseBody
    public ResponseData getCheckCode(@Regular("^(1[0-9]{10})$") String mobile){
        String code = RandomUtil.getRandomCode(4,0);
        // 把验证码5213发送到手机上.
        System.out.println("验证码: "+code+" 已经发送到手机 "+ mobile+"上了");
        // 把验证码保存在redis缓存中
        JedisUtil.set(mobile,code,60);
        return successData.setData(code);
    }
}
