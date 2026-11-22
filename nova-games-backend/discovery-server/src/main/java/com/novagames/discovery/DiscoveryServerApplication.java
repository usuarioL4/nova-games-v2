package com.novagames.discovery;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@org.springframework.cloud.netflix.eureka.server.EnableEurekaServer @SpringBootApplication public class DiscoveryServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class,args);
    }
}
