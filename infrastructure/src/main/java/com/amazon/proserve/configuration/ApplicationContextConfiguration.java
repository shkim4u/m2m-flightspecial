package com.amazon.proserve.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.amazon.proserve.infrastructure")
@Import(value = { DomainContextConfig.class })
public class ApplicationContextConfiguration {
}
