package com.it342g02.vibecoder.edureach;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EduReachApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduReachApplication.class, args);
    }
}
