package com.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 业务处理
 *
 * @author zhangzhaohui
 */
@Slf4j
@Service
public class SockerHandlerService {

    public void dockSocketMessage(String msg) {
        log.info("收到消息：{}", msg);
    }

}
