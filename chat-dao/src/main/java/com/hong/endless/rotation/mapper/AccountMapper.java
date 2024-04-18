package com.hong.endless.rotation.mapper;

import com.hong.endless.rotation.dto.AccountDTO;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.provider.base.BaseInsertProvider;

public interface AccountMapper extends Mapper<AccountDTO> {
    @Override
    @InsertProvider(type = BaseInsertProvider.class, method = "dynamicSQL")
    @Options(useGeneratedKeys = true, keyProperty = "accountId")
    int insertSelective(AccountDTO accountDTO);

    List<AccountDTO> listById(Collection<Integer> idList);

    int insertList(Collection<AccountDTO> list);
}