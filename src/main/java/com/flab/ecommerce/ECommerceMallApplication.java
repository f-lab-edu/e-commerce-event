package com.flab.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ECommerceMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceMallApplication.class, args);
    }

}
