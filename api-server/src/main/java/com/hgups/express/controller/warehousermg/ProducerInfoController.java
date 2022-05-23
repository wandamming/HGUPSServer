package com.hgups.express.controller.warehousermg;

import com.hgups.express.domain.ProductInfo;
import com.hgups.express.domain.Response;
import com.hgups.express.domain.param.DeleteProducerParam;
import com.hgups.express.domain.param.ProductInfoListParam;
import com.hgups.express.domain.param.ProductInfoParam;
import com.hgups.express.domain.param.UpdateProductStateParam;
import com.hgups.express.service.warehousemgi.ProductInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fanc
 * 2020/9/16 0016-17:02
 */
@Api(description = "海外仓产品API")
@Slf4j
@RestController
@RequestMapping("/producerInfo")
public class ProducerInfoController {

    @Resource
    private ProductInfoService productInfoService;


    @ApiOperation(value = "添加修改产品信息")
    @PostMapping("/addOrUpdateProductInfo")
    public Response addOrUpdateProductInfo(@RequestBody ProductInfoParam param){
        Response response = new Response();
        Integer integer = productInfoService.addOrUpdateProductInfo(param);
        if (1==integer){
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }
        else if (3==integer){
            response.setStatusCode(200);
            response.setMsg("添加成功");
            return response;
        }
        else if (2==integer){
            response.setStatusCode(201);
            response.setMsg("修改失败");
            return response;
        }else if (4==integer){
            response.setStatusCode(202);
            response.setMsg("添加失败");
            return response;
        }else {
            response.setStatusCode(203);
            response.setMsg("产品SkuCode已存在");
            return response;
        }
    }

    @ApiOperation(value = "删除产品")
    @PostMapping("/deleteProductInfo")
    public Response deleteProductInfo(@RequestBody DeleteProducerParam param){
        Response response = new Response();
        boolean b = productInfoService.deleteProductInfo(param.getIds());
        if (b){
            response.setStatusCode(200);
            response.setMsg("删除成功");
            return response;
        }
        response.setStatusCode(201);
        response.setMsg("删除失败：库存为0的产品才允许被删除");
        return response;
    }

    @ApiOperation(value = "产品列表")
    @PostMapping("/productInfoList")
    public Response productInfoList(@RequestBody ProductInfoListParam param){
        Response response = new Response();
        List<ProductInfo> productInfos = productInfoService.productInfoList(param);
        Map<Object,Object> result = new HashMap<>();
        int total = productInfoService.productInfoCount(param);
        result.put("total",total);
        result.put("size",param.getSize());
        result.put("current",param.getCurrent());
        result.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总条数
        result.put("records",productInfos);
        response.setStatusCode(200);
        response.setData(result);
        return response;
    }
    @ApiOperation(value = "剩余产品大于零的列表")
    @PostMapping("/productInfoListGtZero")
    public Response productInfoListGtZero(@RequestBody ProductInfoListParam param){
        Response response = new Response();
        List<ProductInfo> productInfos = productInfoService.productInfoListGtZero(param);
        Map<Object,Object> result = new HashMap<>();
        int total = productInfoService.productInfoCountGtZero(param);
        result.put("total",total);
        result.put("size",param.getSize());
        result.put("current",param.getCurrent());
        result.put("pages",(total%param.getSize())==0?total/param.getSize():total/param.getSize()+1);//总条数
        result.put("records",productInfos);
        response.setStatusCode(200);
        response.setData(result);
        return response;
    }

    @ApiOperation(value = "修改产品状态")
    @PostMapping("/updateProductState")
    public Response updateProductState(@RequestBody UpdateProductStateParam param){
        Response response = new Response();
        boolean b = productInfoService.updateProductState(param);
        if (b){
            response.setStatusCode(200);
            response.setMsg("修改成功");
            return response;
        }
        response.setStatusCode(201);
        response.setMsg("修改失败");
        return response;
    }


    @ApiOperation(value = "上传产品图片")
    @PostMapping("/uploadProductImg")
    public Response uploadProductImg(MultipartFile file,HttpServletRequest request){

        Response response = new Response();
        if (file == null) {
            response.setStatusCode(600);
            response.setMsg("文件丢失,请重新上传");
            return response;
        }
        //文件名
        String filename = file.getOriginalFilename();

        FileOutputStream outputStream = null;
        InputStream fileSource = null;
        String tempFileName = "";
        String tempFileNameUrl = "";
        String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +"/warehouse/img/";//存储路径

        String path = request.getSession().getServletContext().getRealPath("warehouse/img/"); //文件存储位置
        //String path = PathUtils.resDir + "warehouse/img/";
        try {
            fileSource= file.getInputStream();
            tempFileName = path+file.hashCode()+System.currentTimeMillis()+filename;
            tempFileNameUrl = returnUrl+file.hashCode()+System.currentTimeMillis()+filename;

            System.out.println("文件地址--======"+tempFileName);
            System.out.println("http地址--------"+tempFileNameUrl);
            //tempFile指向临时文件
            File tempFile = new File(tempFileName);
            if(!tempFile.exists()) {
                tempFile.getParentFile().mkdirs();
                tempFile.createNewFile();
            }
            //outputStream文件输出流指向这个临时文件

            outputStream = new FileOutputStream(tempFile);

            byte[]  b = new byte[1024];
            int n;
            while((n=fileSource.read(b)) != -1){
                outputStream.write(b, 0, n);
            }
        }catch (IOException e){
            e.printStackTrace();
            response.setStatusCode(203);
            response.setMsg("文件上传出错");
            return response;
        }finally {
            //关闭输入输出流
            if (null!=outputStream){
                try {
                    outputStream.close();
                    fileSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Map<String,String> result = new HashMap<>();
        result.put("imgUrl",tempFileNameUrl);
        response.setStatusCode(200);
        response.setMsg("上传成功");
        response.setData(result);
        return response;
    }
}
