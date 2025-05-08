package com.flab.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        exclude = {
                org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration.class,
                org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration.class
        }
)
@EnableJpaAuditing
@EnableScheduling
public class ECommerceMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceMallApplication.class, args);
    }

}
