package com.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BusinessService {

    public void msgHandle(String msg) {
        log.info("收到消息：{}", msg);
    }

}
