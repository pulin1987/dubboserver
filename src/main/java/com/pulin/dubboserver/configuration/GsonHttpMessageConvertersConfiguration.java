package com.pulin.dubboserver.configuration;

import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.util.ArrayList;
import java.util.Collection;


@Configuration
public class GsonHttpMessageConvertersConfiguration {
    /*
     * GsonHttpMessageConverter 转换配置
     */
    @Bean
    public HttpMessageConverters gsonHttpMessageConverters() {
        Collection<HttpMessageConverter<?>> messageConverters = new ArrayList();
        // Gson 配置
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        messageConverters.add(gsonHttpMessageConverter);
        return new HttpMessageConverters(true, messageConverters);
    }
}
