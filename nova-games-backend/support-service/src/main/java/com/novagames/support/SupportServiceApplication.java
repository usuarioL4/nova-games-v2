package com.novagames.support;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.novagames.support","com.novagames.common"}) public class SupportServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SupportServiceApplication.class,args);
    }
}
