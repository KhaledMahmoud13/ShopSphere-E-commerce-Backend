package com.khaled.shopsphere.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableAsync
@Configuration
public class AsyncConfig {
    @Bean(name = "imageUploadExecutor")
    public Executor imageUploadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
