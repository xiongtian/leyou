package com.leyou.upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author xiongtian
 * @create 2020/8/6-8:33
 */
@Configuration
public class LeyouCorsConfiguration {

    @Bean
    public CorsFilter corsFilter(){

        CorsConfiguration configuration= new CorsConfiguration();
        configuration.addAllowedOrigin("http://manage.leyou.com");
        configuration.setAllowCredentials(true);
        //3) 允许的请求方式
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource configurationSource=new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",configuration);
        return new CorsFilter(configurationSource);
    }
}
