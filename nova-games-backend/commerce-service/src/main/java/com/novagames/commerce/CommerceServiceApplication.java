package com.novagames.commerce;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.novagames.commerce","com.novagames.common"}) public class CommerceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommerceServiceApplication.class,args);
    }
}
