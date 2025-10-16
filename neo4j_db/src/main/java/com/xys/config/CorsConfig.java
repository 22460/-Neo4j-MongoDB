package com.xys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS配置类，用于处理跨域请求
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 创建CORS配置对象
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        
        // 设置允许所有源进行跨域请求
        corsConfiguration.addAllowedOriginPattern("*");
        
        // 设置允许的HTTP方法
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // 设置允许的请求头
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With"));
        
        // 允许客户端读取响应头中的所有信息
        corsConfiguration.setExposedHeaders(Arrays.asList("Content-Length", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers"));
        
        // 允许发送凭证信息（如cookies）
        corsConfiguration.setAllowCredentials(true);
        
        // 设置预检请求的有效期（秒）
        corsConfiguration.setMaxAge(3600L);
        
        // 创建URL映射源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // 对所有路径应用CORS配置
        source.registerCorsConfiguration("/**", corsConfiguration);
        
        // 返回配置好的CORS过滤器
        return new CorsFilter(source);
    }
}