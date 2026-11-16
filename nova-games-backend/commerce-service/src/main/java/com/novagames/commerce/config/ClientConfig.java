package com.novagames.commerce.config;
import org.springframework.context.annotation.*;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
@Configuration public class ClientConfig {
    @Bean @LoadBalanced RestClient.Builder restClientBuilder() {
        var f=new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(3000);
        f.setReadTimeout(5000);
        return RestClient.builder().requestFactory(f);
    }
}
