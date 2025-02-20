package com.example.keepstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KeepStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeepStockApplication.class, args);
    }

}
