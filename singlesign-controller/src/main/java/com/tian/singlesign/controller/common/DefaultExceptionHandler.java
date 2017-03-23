package com.tian.singlesign.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.tian.singlesign.service.common.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/3/22 0022.
 */
public class DefaultExceptionHandler implements HandlerExceptionResolver {
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        ModelAndView mv = new ModelAndView();
        //  使用response返回
        response.setStatus(HttpStatus.OK.value()); //设置状态码
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //设置ContentType
        response.setCharacterEncoding("UTF-8"); //避免乱码
        response.setHeader("Cache-Control", "no-cache, must-revalidate");


        try{
            e.printStackTrace();
            if(e instanceof BusinessException){
                BusinessException be = (BusinessException)e;
                // 如果是我们返回的业务异常则进行封装
                response.getWriter().write(JSONObject.toJSONString(new ResponseData(be.getErrorCode(),"failed",be.getErrorMessage())));
            }else{
                //返回统一内部错误类型
                response.getWriter().write(JSONObject.toJSONString(new ResponseData(500,"failed",e.getClass().getName())));
                // 返回特定页面
//                mv.setViewName("index.jsp");
                return mv;
            }
        }catch (Exception e2){
            e2.printStackTrace();
        }
        return mv;
    }

}
