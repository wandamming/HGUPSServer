package com.hgups.express.controller;

import com.hgups.express.domain.Response;
import com.hgups.express.domain.ResponseMetaEnum;
import com.hgups.express.exception.AuditException;
import com.hgups.express.exception.MyException;
import com.hgups.express.exception.NoAuthException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
//该注解定义全局异常处理类
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = IncorrectCredentialsException.class)
    @ResponseBody
    public Response defaultIncorrectCredentialsExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        Response response = new Response();
        response.setStatusCode(ResponseMetaEnum.PASSWORD_ERROR.getCode());
        response.setMsg("密码错误");
        return response;
    }

    @ExceptionHandler(value = UnknownAccountException.class)
    @ResponseBody
    public Response defaultUnknownAccountExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        Response Response = new Response();
        Response.setStatusCode(ResponseMetaEnum.PARAM_ERROR.code);
        return Response;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Response defaultMethodArgumentNotValidExceptionHandler(HttpServletRequest req, MethodArgumentNotValidException e) {

        Response Response = new Response();
        Response.setStatusCode(ResponseMetaEnum.PARAM_ERROR.code);
        Response.setMsg("参数错误："+e.getBindingResult().getFieldError().getDefaultMessage());
        return Response;
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public Response defaultMissingServletRequestParameterExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        Response Response = new Response();
        Response.setStatusCode(ResponseMetaEnum.PARAM_ERROR.code);
        return Response;
    }

    @ExceptionHandler(value = NoAuthException.class)
    @ResponseBody
    public Response NoAuthExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        Response response = new Response();
        response.setStatusCode(ResponseMetaEnum.NO_AUTH.code);
        response.setMsg("没有登陆，请先登陆");
        return response;
    }

    @ExceptionHandler(value = org.apache.shiro.session.UnknownSessionException.class)
    @ResponseBody
    public Response UnknownSessionException(HttpServletRequest req, Exception e) throws Exception {
        Response response = new Response();
        response.setStatusCode(ResponseMetaEnum.TOKEN_FAILURE.code);
        response.setMsg("token已失效");
        return response;
    }

    //@ExceptionHandler 该注解声明异常处理方法
    //value=AuditException.classb表示处理抛出的AuditException异常
    @ExceptionHandler(value = AuditException.class)
    @ResponseBody
    public Response defaultErrorHandler(HttpServletRequest req, Exception e)  throws Exception {
        Response response = new Response();
        response.setStatusCode(ResponseMetaEnum.NO_AUDIT.code);
        response.setMsg("账户未审核");
        //打印异常信息
        log.info("账户未审核", e);
        return response;
    }

    @ExceptionHandler(value = MyException.class)
    @ResponseBody
    public Response MyExceptionHandler(HttpServletRequest req, MyException e) {
        Response response = new Response();
        response.setResponseByErrorMsg(e.getMessage());
        return response;
    }
}
