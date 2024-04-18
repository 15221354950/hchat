package com.hong.service;

import com.alibaba.fastjson.JSON;
import com.hong.endless.rotation.dto.AccountDTO;
import com.hong.endless.rotation.mapper.AccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class DaoService {

    private static Logger logger = LoggerFactory.getLogger(DaoService.class);

    @Resource
    private AccountMapper accountMapper;

    public void doTest() {
        List<AccountDTO> list = accountMapper.selectAll();
        logger.info("list:{}", JSON.toJSONString(list));
        Object a = accountMapper.listById(Arrays.asList(2));
        logger.info("a:{}", JSON.toJSONString(a));
    }
}
