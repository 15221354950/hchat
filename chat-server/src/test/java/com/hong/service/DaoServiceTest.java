package com.hong.service;


import com.hong.Application;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.runner.RunWith;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class DaoServiceTest {
    private static Logger logger = LoggerFactory.getLogger(DaoServiceTest.class);

    @Resource
    private DaoService daoService;


    @Test
    public void doTest() {
        daoService.doTest();
    }
}