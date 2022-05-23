package com.hgups.express.domain.param;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fanc
 * 2020/12/29-11:41
 */
@Data
public class ImportMoreSkuWarehouseInfoParam {
    //序号
    public Integer nid;
    public List<ProductionVo> productionVo = new ArrayList<>();
    public boolean checkAddress;
    /**
     *  收件人信息
     */
    //姓名
    private String name;
    //公司
    private String company;
    //国家
    private String countries;
    //省份(英)
    private String provinceEname;
    //城市(英)
    private String cityEname;
    //邮政编码一
    private String postalCode;
    //邮政编码二
    private String postalCodet;
    //门牌号
    private String addressOne;
   //主要地址
    private String addressTwo;
    //电话
    private String phone;
    //电话前缀
    private String phonePrefix;
    //邮箱
    private String email;

    @Data
    public static class ProductionVo {
        public int index;
        public boolean valid = true;
        //产品ID
        public Long producerId;
        //sku/产品编码
        public String skuCode;
        //出库数量
        public Integer producerNumber;
        //英文名称
        public String ename;
        //中文名称
        public String cname;
        //单位重量
        public double unitWeight;
        //库存数量
        public Integer inventoryNumber;
    }

}
