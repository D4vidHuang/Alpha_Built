package com.ecadi.alphabuiltbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan
@EntityScan
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.ecadi.alphabuiltbackend"})
public class AlphaBuiltBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlphaBuiltBackendApplication.class, args);
    }

}
