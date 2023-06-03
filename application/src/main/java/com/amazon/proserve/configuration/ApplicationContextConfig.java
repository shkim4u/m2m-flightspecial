package com.amazon.proserve.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = { DomainContextConfig.class })
@ComponentScan(basePackages = { "com.amazon.proserve.application" })
public class ApplicationContextConfig {
}
