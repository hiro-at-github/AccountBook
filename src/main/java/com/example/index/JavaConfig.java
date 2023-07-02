package com.example.index;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.bufferings.thymeleaf.extras.nl2br.dialect.Nl2brDialect;

@Configuration
public class JavaConfig {

    @Bean
    public Nl2brDialect dialect() {
        return new Nl2brDialect();
    }
}
