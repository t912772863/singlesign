<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta name="renderer" content="webkit" />
    <title>登录页面</title>
    <script src="resource/js/common/jquery-1.10.2.min.js"></script>
</head>
<body style="background: none;">
    <div id="div_1">
    <form id="login_in" >
        <div>用户名: <input type="text" name="userName"/></div>
        <div>密码: <input type="password" name="password"/></div>
        <input onclick="changeLoginType();" type="button" value="切换为手机号登录"/>
    </form>
    <button onclick="login();">登录</button>
    </div>

    <div id="div_2" hidden>
    <form id="login_in_2" >
        <div>手机号: <input type="text" name="mobile"/></div>
        <div>验证码: <input id="code" type="password" name="code"/><input type="button" value="获取验证码" onclick="getCode();"></div>
        <input onclick="changeLoginType2();" type="button" value="切换为用户名登录"/>
    </form>
    <button onclick="login2();">登录</button>
    </div>
</body>

<script type="text/javascript">
    function login(){
        $.ajax({
            url: "/login/login",
            type:'post',
            data: $("#login_in").serialize(),
            success: function (data) {
                if(data.code == 200){
//                    setCookie("user_id",data.data.id.toString());
//                    alert(getCookie("user_id").toString());
//                    user = data.data;
                    window.location.href = "view/main.jsp"
                }else{
                    alert(data.data);
                }
            }
        });
    }

    function login2(){
        alert("login2");
        $.ajax({
            url: '/login/login_code',
            type:'post',
            data: $("#login_in_2").serialize(),
            success: function (data) {
                if(data.code == 200){
                    // 把userId放入cookie中,方便不同的页面共享数据
                    setCookie("user_id",data.data.id);
                    user = data.data;
                    window.location.href = "view/main.jsp"
                }else{
                    alert(data.data);
                }
            }
        });
    }

    function changeLoginType() {
        $("#div_1").hide();
        $("#div_2").show();
    }
    function changeLoginType2() {
        $("#div_1").show();
        $("#div_2").hide();
    }

    function getCode() {
        $.ajax({
            url: '/login/get_check_code',
            type:'post',
            data: {mobile:$("input[name='mobile']").val()},
            success: function (data) {
                if(data.code == 200){
                    alert(data.data);
                }else{
                    alert(data.data);
                }
            }
        });
    }


</script>
</html>
