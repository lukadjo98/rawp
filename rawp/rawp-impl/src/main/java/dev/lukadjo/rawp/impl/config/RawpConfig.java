package dev.lukadjo.rawp.impl.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@Configurable
@ComponentScan(basePackages = {"dev.lukadjo.rawp"})
public class RawpConfig {
}
