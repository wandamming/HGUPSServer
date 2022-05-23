package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.plugins.Page;
import com.hgups.express.constant.ResponseCode;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.*;
import com.hgups.express.domain.ApiAccount;
import com.hgups.express.service.waybillmgi.ApiAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wandaming
 * 2021/7/16-14:40
 */

@Api(description = "API账户列表")
@Slf4j
@RestController
@RequestMapping("/apiAccount")
public class ApiAccountController {

    @Resource
    private ApiAccountService apiAccountService;

    @ApiOperation(value = "根据名字获取Api账户信息")
    @PostMapping("/selectByMap")
    public Response selectByMap(@ApiParam(value = "uname") @RequestParam("uname") String uname){
        List<ApiAccount> apiAccount = apiAccountService.selectByMap(uname);
        return new Response(apiAccount);
    }

    @ApiOperation("分页显示所有Api账户信息")
    @PostMapping(value = "/allApiAccount")
    public Response allApiAccount(@RequestBody PageParam pageParam) {
        Response response = new Response();
        Page<ApiAccount> pageList = apiAccountService.getPageList(pageParam);
        response.setData(pageList);
        return response;
    }

    @ApiOperation(value = "新建AIP账户信息，输入用户账号和备注")
    @PostMapping("/addApiAccount")
    public boolean addApiAccount(@ApiParam(value = "发件人参数") @RequestBody AddApiAccountParam param){
        try{
            apiAccountService.insertApiAccount(param);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @ApiOperation(value = "删除AIP账户信息")
    @PostMapping("/deleteApiAccount")
    public Response deleteApiAccount(@ApiParam(value = "id") @RequestParam("id") Integer id){
        Response response = new Response();
        boolean flag = apiAccountService.deleteApiAccount(id);
        if(flag){
            response.setStatusCode(ResponseCode.SUCCESS_CODE);
            response.setMsg("删除成功");
            return response;
        }
        response.setStatusCode(ResponseCode.FAILED_CODE);
        response.setMsg("删除失败");
        return response;
    }


}
