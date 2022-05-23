package com.hgups.express.controller.waybillmg;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hgups.express.domain.*;
import com.hgups.express.domain.param.*;
import com.hgups.express.service.usermgi.UserService;
import com.hgups.express.service.waybillmgi.UserBatchService;
import com.hgups.express.service.waybillmgi.UserSacksService;
import com.hgups.express.service.waybillmgi.WayBillService;
import com.hgups.express.util.BatchLabelUtils;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.SacksLabelUtils;
import com.hgups.express.util.ShiroUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/7/4 0004-18:26
 */
@Api(description = "用户批次API")
@Slf4j
@RestController
@RequestMapping("/userBatch")
public class UserBatchController {

    @Resource
    private UserBatchService userBatchService;
    @Resource
    private UserSacksService userSacksService;
    @Resource
    private WayBillService wayBillService;
    @Resource
    private UserService userService;


    @ApiOperation(value = "创建批次API")
    @PostMapping("/setUserBatch")
    public Response setUserBatch(@RequestBody CreateUserBatchParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        UserBatch userBatch = userBatchService.setUserBatch(param, loginUserId);
        if(null==userBatch){
            response.setStatusCode(300);
            response.setMsg("批次创建失败");
            return response;
        }
        UserBatchParam batchParam = DomainCopyUtil.map(userBatch, UserBatchParam.class);
        response.setStatusCode(200);
        response.setData(batchParam);
        return response;
    }

    @PostMapping("/setWayBillToUserSacks")
    @ApiOperation(value = "批次提交")
    public Response submitUserBatch(@RequestBody SubmitUserBatchListParam paramList){
        Long loginUserId = ShiroUtil.getLoginUserId();
        User user = userService.selectById(loginUserId);
        Response response = new Response();
        List<SubmitUserBatchParam> params = paramList.getSacksWayBillIds();
        int batchId = paramList.getBatchId();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id",loginUserId);
        wrapper.eq("user_batch_id",batchId);
        List<WayBill> wayBillList1 = wayBillService.selectList(wrapper);
        for (WayBill wayBill:wayBillList1){
            wayBill.setUserBatchId(0);
            wayBill.setUserSacksId(0);
            wayBillService.updateById(wayBill);
        }

        float sumSacksPrice=0;//批次总金额
        int sumWayBillNumber=0;//批次运单总数
        for (SubmitUserBatchParam param:params) {
            int sacksId = param.getSacksId();//用户麻袋id
            List<Integer> wayBillIds = param.getWayBillIds();//运单id

            //当前麻袋麻袋信息
            UserSacks userSacks = userSacksService.selectById(sacksId);
            //运单信息
            List<WayBill> wayBillList = new ArrayList<>();
            if (wayBillIds.size()!=0){
                EntityWrapper<WayBill> wrapper2 = new EntityWrapper<>();
                wrapper2.in("id",wayBillIds);
                wayBillList = wayBillService.selectList(wrapper2);
            }

            float sumPrice = 0;//麻袋中运单总金额
            float sumBillWeight = 0;//麻袋中运单总重量
            for (WayBill wayBill:wayBillList){
                sumPrice+=wayBill.getPrice();
                sumBillWeight+=wayBill.getBillWeight();
                wayBill.setUserSacksId(sacksId);
                wayBill.setUserBatchId(batchId);
                wayBillService.updateById(wayBill);
            }
            userSacks.setBillWeight(sumBillWeight);
            userSacks.setSumPrice(sumPrice);
            userSacks.setParcelNumber(wayBillList.size());//麻袋总运单数量
            userSacks.setState("2");
            String userSacksCode = SacksLabelUtils.createUserSacksPDF(userSacks, user.getUsername(), user.getCompany());//生成PDF
            userSacks.setCoding(userSacksCode);
            userSacksService.updateById(userSacks);
            sumSacksPrice+=sumPrice;//批次总金额
            sumWayBillNumber+=wayBillList.size();//批次运单总数
        }
        if (batchId==0){
            response.setMsg("批次创建失败");
            response.setStatusCode(300);
        }else{
            //当前批次信息
            UserBatch userBatch = userBatchService.selectById(batchId);
            userBatch.setTotalAmount(sumSacksPrice);
            userBatch.setWaybillNumber(sumWayBillNumber);
            userBatch.setSacksNumber(params.size());
            userBatch.setState("2");
            String userSacksCode = BatchLabelUtils.createUserBatchPDF(userBatch, user.getUsername(), user.getCompany());//生成PDF
            userBatch.setCoding(userSacksCode);
            userBatchService.updateById(userBatch);
        }
        response.setMsg("批次创建成功");
        response.setStatusCode(200);
        return response;
    }

    @ApiOperation(value = "获取当前用户全部已创建批次")
    @PostMapping("/getAllUserBatch")
    public Response getAllUserBatch(@RequestBody PageParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Response response = new Response();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id",loginUserId).or("state","2").or("state","3").orderBy("id", false);;
        List<UserBatch> allUserBatch = userBatchService.getAllUserBatch(param, wrapper);

        Map<Object,Object> result = new HashMap<>();
        int total = userBatchService.selectCount(wrapper);
        result.put("total",total);
        result.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总条数
        result.put("records",allUserBatch);
        response.setStatusCode(200);
        response.setData(result);
        return response;
    }

    @ApiOperation(value = "删除批次")
    @PostMapping("/deleteUserBatch")
    public Response deleteUserBatch(@RequestBody IdParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        int batchId = param.getId();
        Response response = new Response();
        UserBatch userBatch = userBatchService.selectById(batchId);
        if (null!=userBatch){
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("user_batch_id",batchId);
            wrapper.eq("user_id",loginUserId);
            List<UserSacks> sacksList = userSacksService.selectList(wrapper);
            for (UserSacks userSacks:sacksList){
                EntityWrapper wrapper1 = new EntityWrapper();
                wrapper1.eq("user_batch_id",batchId);
                wrapper1.eq("user_id",loginUserId);
                wrapper1.eq("user_sacks_id",userSacks.getId());
                List<WayBill> wayBillList = wayBillService.selectList(wrapper1);
                for (WayBill wayBill:wayBillList){
                    wayBill.setUserBatchId(0);
                    wayBill.setUserSacksId(0);
                    wayBillService.updateById(wayBill);
                }
                userSacksService.deleteById(userSacks.getId());
            }
            userBatchService.deleteById(userBatch.getId());
            response.setStatusCode(200);
            response.setMsg("删除成功");
        }else {
            response.setStatusCode(300);
            response.setMsg("删除失败");
        }
        return response;
    }

}
