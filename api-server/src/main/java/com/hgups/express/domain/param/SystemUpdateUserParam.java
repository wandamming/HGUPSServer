package com.hgups.express.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author fanc
 * 2020/7/30 0030-11:17
 */
@Data
public class SystemUpdateUserParam {

    private Long id;
    //private String password;
    private String phone;
    private String phonePrefix;
    private String company;
    private String email;
    private int state;
    private Integer handleId;//操作费用ID
    private boolean customsPrice=false;//是否收取海关费用

    private List<Integer> roleIds;


}
