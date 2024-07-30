package org.kt.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Slf4j
public class DBConfig {

    @Primary
    @Bean
    public BasicDataSource soulDataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        String url = String.format("jdbc:postgresql://%s:%d/%s",
                    System.getenv("DB_HOST"),
                    5432,
                    System.getenv("DB_NAME"));
        dataSource.setUrl(url);
        dataSource.setUsername(System.getenv("POSTGRES_USER"));
        dataSource.setPassword(System.getenv("POSTGRES_PASSWORD"));
        log.debug(dataSource.getUrl());
        return dataSource;
    }
}

