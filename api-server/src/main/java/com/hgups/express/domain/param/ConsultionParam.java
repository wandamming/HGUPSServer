package com.hgups.express.domain.param;

import lombok.Data;

@Data
public class ConsultionParam {
    public String name;
    public String telephone;
    public String email;
    public String industry;

    public String company;
    public int scale;
    public String content;
}
