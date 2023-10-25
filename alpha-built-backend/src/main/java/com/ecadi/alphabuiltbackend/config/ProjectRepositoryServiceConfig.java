package com.ecadi.alphabuiltbackend.config;

import com.ecadi.alphabuiltbackend.domain.project.ProjectRepositoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ProjectRepositoryServiceConfig {

    @Bean
    public ProjectRepositoryService getProjectRepositoryService() {
        return new ProjectRepositoryService();
    }

}

