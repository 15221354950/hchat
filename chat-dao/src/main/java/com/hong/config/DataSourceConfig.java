package com.hong.config;

import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

@Configuration
@MapperScan(basePackages = {"com.hong.endless.rotation.mapper"})
public class DataSourceConfig {

}
