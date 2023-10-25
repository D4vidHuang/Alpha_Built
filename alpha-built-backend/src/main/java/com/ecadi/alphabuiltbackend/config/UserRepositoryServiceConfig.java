package com.ecadi.alphabuiltbackend.config;

import com.ecadi.alphabuiltbackend.domain.user.UserRepositoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserRepositoryServiceConfig {
    @Bean
    public UserRepositoryService getUserRepositoryService() {
        return new UserRepositoryService();
    }

}

