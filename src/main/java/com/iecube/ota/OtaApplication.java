package com.iecube.ota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OtaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtaApplication.class, args);
    }

}
