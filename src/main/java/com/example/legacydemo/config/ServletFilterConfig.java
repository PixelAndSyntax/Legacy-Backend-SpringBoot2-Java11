package com.example.legacydemo.config;

import com.example.legacydemo.servlet.LegacySecurityFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.Filter;

/**
 * Servlet filter configuration using javax.servlet APIs.
 *
 * MIGRATION CHALLENGE: FilterRegistrationBean<Filter> needs javax.servlet.Filter
 * which must be migrated to jakarta.servlet.Filter in Spring Boot 3.x
 */
@Configuration
public class ServletFilterConfig {

    /**
     * Create the legacy security filter bean
     */
    @Bean
    public LegacySecurityFilter legacySecurityFilter() {
        return new LegacySecurityFilter();
    }

    /**
     * Register legacy security filter
     *
     * MIGRATION NOTE: The Filter type parameter changes from
     * javax.servlet.Filter to jakarta.servlet.Filter
     */
    @Bean
    public FilterRegistrationBean<Filter> legacySecurityFilterRegistration(LegacySecurityFilter filter) {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*");
        registration.setName("legacySecurityFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
