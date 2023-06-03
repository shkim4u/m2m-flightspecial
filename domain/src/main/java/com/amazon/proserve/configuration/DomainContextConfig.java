package com.amazon.proserve.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {})
@ComponentScan(basePackages = { "com.amazon.proserve.domain" })
public class DomainContextConfig {
}
