package com.leyou.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 配置类：利用CORS解决跨域的问题
 * @author xiongtian
 * @create 2020/4/2-8:32
 */
@Configuration
public class LeyouCorsConfiguration {

    @Bean
    public CorsFilter corsFilter(){

        //初始化cors配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        //允许跨域的域名，如果要携带cookie,不能写*。代表所有域名都能跨域访问
        configuration.addAllowedOrigin("http://manage.leyou.com");
        configuration.setAllowCredentials(true);//是否允许携带cookie
        //configuration.addAllowedMethod("*");//代表所有的请求方法： GET POST PUT Delete...

        //3) 允许的请求方式
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("HEAD");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedHeader("*");//允许携带任何头信息


        //初始化cors配置源对象
        UrlBasedCorsConfigurationSource configSource=new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**",configuration);

        //返回cors，参数：cors配置源对象
        return new CorsFilter(configSource);
    }
}
