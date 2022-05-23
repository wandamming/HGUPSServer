package com.hgups.express.service.warehousemgi;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hgups.express.domain.ProductInfo;
import com.hgups.express.domain.param.*;
import com.hgups.express.domain.vo.OrderInfoVo;
import com.hgups.express.domain.vo.ProductInfoVo;
import com.hgups.express.mapper.ProductInfoMapper;
import com.hgups.express.util.DomainCopyUtil;
import com.hgups.express.util.ShiroUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author fanc
 * 2020/9/16 0016-16:45
 */
@Service
@Transactional
public class ProductInfoService extends ServiceImpl<ProductInfoMapper,ProductInfo> {

    @Resource
    private ProductInfoMapper productInfoMapper;

    //查看订单中的产品信息
    public Page<ProductInfoVo> getOrderProduct(IdPageParam param){

        Page<ProductInfoVo> page = new Page<>();
        page.setCurrent(param.getCurrent());
        page.setSize(param.getSize());
        List<ProductInfoVo> vos = productInfoMapper.getOrderProduct(page, param);
        int total = productInfoMapper.getOrderProductCount(param);
        page.setTotal(total);
        page.setRecords(vos);
        return page;
    }

    //添加修改产品信息
    public Integer addOrUpdateProductInfo(ProductInfoParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        ProductInfo productInfo = DomainCopyUtil.map(param, ProductInfo.class);
        EntityWrapper<ProductInfo> wrapper = new EntityWrapper<>();
        if (null!=param.getId()&&param.getId()>0){
            wrapper.ne("id",param.getId());
            wrapper.eq("sku_code",param.getSkuCode());
            ProductInfo productInfo1 = selectOne(wrapper);
            if (productInfo1!=null){
                return 5;//产品sukCode已经存在
            }
            Integer integer = productInfoMapper.updateById(productInfo);
            if (integer>0){
                return 1;//修改成功
            }
            return 2;//修改失败
        }else {
            wrapper.eq("sku_code",param.getSkuCode());
            ProductInfo productInfo1 = selectOne(wrapper);
            if (productInfo1!=null){
                return 5;//产品sukCode已经存在
            }
            productInfo.setUserId(loginUserId.intValue());
            productInfo.setCreateTime(new Date());
            Integer insert = productInfoMapper.insert(productInfo);
            if (insert>0){
                return 3;//添加成功
            }
            return 4;//添加失败
        }
    }

    //修改产品状态
    public boolean updateProductState(UpdateProductStateParam param){
        Long productId = param.getId();
        Integer state = param.getState();
        ProductInfo productInfo = productInfoMapper.selectById(productId);
        if (null!=productInfo){
            productInfo.setState(state);
            return updateById(productInfo);
        }
        return false;
    }

    //删除产品
    public boolean deleteProductInfo(List<Long> ids){
        if(null==ids){
            return false;
        }
        List<ProductInfo> productInfoList = productInfoMapper.selectBatchIds(ids);
        for (ProductInfo productInfo : productInfoList) {
            if(productInfo.getInventoryNumber() > 0) {
                return false;
            }
            productInfo.setFlag(2);
            updateById(productInfo);
        }
        return true;
    }

    //全部产品列表
    public List<ProductInfo> productInfoList(ProductInfoListParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Page<ProductInfo> page = new Page<>(param.getCurrent(),param.getSize());
        EntityWrapper<ProductInfo> wrapper = new EntityWrapper<>();
        wrapper.orderBy("create_time",false);
        wrapper.eq("user_id",loginUserId.intValue());
        wrapper.eq("flag",1);
        if (!StringUtils.isEmpty(param.getSkuCode())){
            wrapper.eq("sku_code",param.getSkuCode());
        }
        if (!StringUtils.isEmpty(param.getProductName())){
            wrapper.andNew().eq("e_name",param.getProductName()).or().eq("c_name",param.getProductName());
        }
        Page<ProductInfo> productInfoPage = selectPage(page,wrapper);
        return productInfoPage.getRecords();
    }

    //添加产品列表
    public List<ProductInfo> productInfoListGtZero(ProductInfoListParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        Page<ProductInfo> page = new Page<>(param.getCurrent(),param.getSize());
        EntityWrapper<ProductInfo> wrapper = new EntityWrapper<>();
        wrapper.orderBy("create_time",false);
        wrapper.eq("flag",1);
        wrapper.eq("user_id",loginUserId.intValue());
        wrapper.gt("inventory_number",0);
        if (!StringUtils.isEmpty(param.getSkuCode())){
            wrapper.eq("sku_code",param.getSkuCode());
        }
        if (!StringUtils.isEmpty(param.getProductName())){
            wrapper.andNew().eq("e_name",param.getProductName()).or().eq("c_name",param.getProductName());
        }
        Page<ProductInfo> productInfoPage = selectPage(page,wrapper);
        return productInfoPage.getRecords();
    }

    //产品数量
    public Integer productInfoCountGtZero(ProductInfoListParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        EntityWrapper<ProductInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id",loginUserId.intValue());
        wrapper.eq("flag",1);
        wrapper.gt("inventory_number",0);
        if (!StringUtils.isEmpty(param.getSkuCode())){
            wrapper.eq("sku_code",param.getSkuCode());
        }
        if (!StringUtils.isEmpty(param.getProductName())){
            wrapper.andNew().eq("e_name",param.getProductName()).or().eq("c_name",param.getProductName());
        }
        return productInfoMapper.selectCount(wrapper);
    }

    //产品数量
    public Integer productInfoCount(ProductInfoListParam param){
        Long loginUserId = ShiroUtil.getLoginUserId();
        EntityWrapper<ProductInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id",loginUserId.intValue());
        wrapper.eq("flag",1);
        if (!StringUtils.isEmpty(param.getSkuCode())){
            wrapper.eq("sku_code",param.getSkuCode());
        }
        if (!StringUtils.isEmpty(param.getProductName())){
            wrapper.andNew().eq("e_name",param.getProductName()).or().eq("c_name",param.getProductName());
        }
        return productInfoMapper.selectCount(wrapper);
    }


}
