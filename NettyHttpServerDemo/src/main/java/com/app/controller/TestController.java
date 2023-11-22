package com.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * mvc测试
 *
 * @author ch
 * @date 2023/11/21 10:39
 */
@Slf4j
@RestController
@RequestMapping
public class TestController {

    @PostMapping("/a")
    public String testA() throws Exception {
        log.info("模拟mvc /a 路由的处理");
        Thread.sleep(1000);
        return "{\"result\":\"1\",\"errorCode\":\"\",\"errorMsg\":\"\"}";
    }

}
