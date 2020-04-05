package com.aluohe.citycode.controller;

import com.aluohe.citycode.servince.CityServince;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author aluohe
 * @className CityController
 * @projectName city-code
 * @date 2020/4/5 22:35
 * @description
 * @modified_by
 * @version:
 */
@RestController
@RequestMapping(value = "/code")
public class CityController {

    @Autowired
    CityServince cityServince;

    @PostMapping("/jsoup")
    public String jsoup() {


        cityServince.servince();

        return "success";
    }
}
