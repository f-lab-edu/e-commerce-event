package com.flab.ecommerce.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExcutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);            // 기본 스레드 수
        executor.setMaxPoolSize(16);            // 최대 스레드 수
        executor.setQueueCapacity(100);         // 큐에 쌓일 수 있는 작업 수
        executor.setThreadNamePrefix("Async-"); // 스레드 이름
        executor.initialize();
        return executor;
    }
}
