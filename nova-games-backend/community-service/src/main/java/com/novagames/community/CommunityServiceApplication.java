package com.novagames.community;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.novagames.community","com.novagames.common"}) public class CommunityServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityServiceApplication.class,args);
    }
}
