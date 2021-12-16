package com.ruoyan.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Package: com.ruoyan.config
 * @ClassName: MybatisPlusConfig
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/22 16:46
 * @Description: mybatisplus配置类
 */
@Configuration
@MapperScan("com.ruoyan.mapper")
public class MybatisPlusConfig
{
    /**
     * 自定义MybatisPlus拦截器
     *
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor()
    {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        //防止全表更新插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }


    /**
     * 避免分页插件缓存出现问题配置定制器
     *
     * @return ConfigurationCustomer
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer()
    {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }
}
