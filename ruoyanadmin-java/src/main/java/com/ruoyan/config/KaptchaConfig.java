package com.ruoyan.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;


/**
 * @Package: com.ruoyan.config
 * @ClassName: KaptchaConfig
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 16:30
 * @Description: 验证码配置类
 */
@Configuration
public class KaptchaConfig
{
    @Bean
    public DefaultKaptcha producer()
    {
        Properties properties = new Properties();
        //配置生成验证码图片规则信息
        properties.put("kaptcha.border", "no");
        properties.put("kaptcha.textproducer.font.color", "black");
        properties.put("kaptcha.textproducer.char.space", "4");
        properties.put("kaptcha.image.height", "40");
        properties.put("kaptcha.image.width", "120");
        properties.put("kaptcha.textproducer.font.size", "30");

        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }
}
