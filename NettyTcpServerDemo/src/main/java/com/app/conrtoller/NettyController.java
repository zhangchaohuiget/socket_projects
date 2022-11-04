package com.app.conrtoller;

import com.app.netty.NettyServer;
import com.app.utils.AppResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/netty")
public class NettyController {

    @Autowired
    private NettyServer nettyServer;

    @GetMapping("/start")
    public AppResult start() {
        boolean flag = nettyServer.serverStart();
        if (flag) {
            return AppResult.ok("启动成功！");
        } else {
            return AppResult.error("启动失败！");
        }
    }

    @GetMapping("/stop")
    public AppResult stop() {
        boolean flag = nettyServer.serverStop();
        if (flag) {
            return AppResult.ok("关闭成功！");
        } else {
            return AppResult.error("关闭失败！");
        }
    }
}
