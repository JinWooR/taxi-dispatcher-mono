package com.taxidispatcher.shared.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@AutoConfiguration
@ComponentScan(basePackages = "com.taxidispatcher.shared.common")
@EnableMethodSecurity
public class CommonLibAutoConfiguration {
}
