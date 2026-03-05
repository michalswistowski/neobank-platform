package com.michalswistowski.currency_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // here, because sliced test was not passing due to previous annotation on top of main class
public class JpaConfig {
}
