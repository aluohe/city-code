package com.aluohe.citycode.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseName {

    private String code;
    private String name;

    private String parent;


}