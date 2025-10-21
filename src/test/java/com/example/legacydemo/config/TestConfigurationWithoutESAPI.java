package com.example.legacydemo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Test configuration that excludes problematic ESAPI components
 */
@TestConfiguration
@ComponentScan(basePackages = "com.example.legacydemo",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.example.legacydemo.service.ESAPISecurityService.class,
                                com.example.legacydemo.controller.ESAPISecurityController.class
                        })
        })
public class TestConfigurationWithoutESAPI {
}