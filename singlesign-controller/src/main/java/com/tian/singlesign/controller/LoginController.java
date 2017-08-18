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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

/**
 * 与登录相关的控制层(其它不需要做登录校验的请求也放这里,方便统一处理)
 * Created by Administrator on 2016/12/13 0013.
 */
@Controller
@RequestMapping("login")
public class LoginController extends BaseController {
    @Autowired
    private IUserService userService;
    @Autowired
    private ITestRemoteService testRemoteService;

    @RequestMapping("test")
    @ResponseBody
    public ResponseData test() {
        String s = testRemoteService.print("终于成功啦...");
        return success.setData(s);
    }

    @RequestMapping("to_index")
    public ModelAndView toIndex(String url, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //检查用户是否登录了, 如果没有登录, 返回到登录页面, 如果登录了,返回token
        HttpSession session = request.getSession(true);
        if (session.getAttribute("user") == null) {
            // 没有登录
            ModelAndView mav = new ModelAndView("main");
            mav.addObject("fromUrl", url);
            return mav;
        } else {
            // 登录了
            String token = request.getSession().getAttribute("token").toString();
            response.sendRedirect(url + (url.contains("?") ? "&" : "?") + "token=" + token);
        }
        return null;

    }

    /**
     * 检查token是否有效
     *
     * @param request
     */
    @RequestMapping("check_token")
    @ResponseBody
    public ResponseData checkToken(HttpServletRequest request, String token) throws Exception {
        String thisToken = request.getParameter("token");
        return success.setData(token.equals(thisToken));
    }

    /**
     * 用户登录接口(用户名,密码)
     *
     * @param userName
     * @param password
     * @return
     */
    @RequestMapping("login")
    public ResponseData login(@NotNull String userName, @NotNull String password, String url, HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = userService.queryUserByUserNameAndPassword(userName, password);
        if (user != null) {
            // 用户登录成功,把当前登录用户信息放入Session中管理
            request.getSession(true).setAttribute("user", user);
            String token = UUID.randomUUID().toString();

            request.getSession().setAttribute("token", token);
            response.sendRedirect(url + (url.contains("?") ? "&" : "?") + "token=" + token);
            return null;
        }
        return failed.setData("登录失败");
    }

    /**
     * 通过手机号,验证码登录
     *
     * @param mobile
     * @param code
     * @return
     */
    @RequestMapping("login_code")
    @ResponseBody
    public ResponseData loginCode(@Regular("^(1[0-9]{10})$") String mobile, @NotNull String code, HttpServletRequest request) {
        String c = JedisUtil.get(mobile);
        if (c != null && c.equals(code)) {
            User user = userService.queryUserByMobile(mobile);
            request.getSession().setAttribute("user", user);
            return successData.setData(user);
        }
        return failed.setData("验证码无效");
    }

    /**
     * 模拟获取验证码
     *
     * @return
     */
    @RequestMapping("get_check_code")
    @ResponseBody
    public ResponseData getCheckCode(@Regular("^(1[0-9]{10})$") String mobile) {
        String code = RandomUtil.getRandomCode(4, 0);
        // 把验证码5213发送到手机上.
        System.out.println("验证码: " + code + " 已经发送到手机 " + mobile + "上了");
        // 把验证码保存在redis缓存中
        JedisUtil.set(mobile, code, 60);
        return successData.setData(code);
    }
}
