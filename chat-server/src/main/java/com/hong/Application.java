package com.hong;

import com.alibaba.fastjson.JSON;
import com.hong.endless.rotation.mapper.AccountMapper;
import com.hong.socket.NetServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;

@SpringBootApplication
@EnableIntegration
@IntegrationComponentScan
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private NetServer netServer;

    @PostConstruct
    public void init() throws Exception {
        Object o = accountMapper.selectAll();
        logger.info("all:{}", JSON.toJSONString(o));
        netServer.start();
    }

    public static void main(String[] args) throws IOException {
        try {
            SpringApplication.run(Application.class, args);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
