package com.ab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mgnrega.api")
public class MgnregaApiProperties {

    private String baseUrl;
    private String resourceId;
    private String apiKey;
    private String format;
    private int limit;
}
