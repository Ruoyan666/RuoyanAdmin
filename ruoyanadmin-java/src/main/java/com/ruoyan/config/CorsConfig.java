package com.ruoyan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Package: com.ruoyan.config
 * @ClassName: CorsConfig
 * @Author: ruoyan1998
 * @CreateTime: 2021/11/24 17:55
 * @Description: 跨域配置类
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer
{
    private CorsConfiguration buildConfig()
    {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader("Authorization");
        return corsConfiguration;
    }

    /**
     * 跨域过滤器，将所有资源配置跨域配置，允许资源请求通过
     *
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter()
    {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    /**
     * 跨域mapper过滤器
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        //对于所有mapper请求进行跨域请求放行
        registry.addMapping("/**")
                .allowedOrigins("*")
            // .allowCredentials(true)
                //四种请求方法跨域放行
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);
    }
}
