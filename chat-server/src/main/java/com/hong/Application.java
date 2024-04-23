package com.hong;

import com.alibaba.fastjson.JSON;
import com.hong.endless.rotation.mapper.AccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@EnableAutoConfiguration
@SpringBootApplication
@EnableIntegration
public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    @Resource
    private AccountMapper accountMapper;

    @PostConstruct
    public void init() {
        Object o = accountMapper.selectAll();
        logger.info("all:{}", JSON.toJSONString(o));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
