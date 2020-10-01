package it.polito.ai.lab3.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    private String secretKey = "ep?!7uLs8c96R%YH";

    //validity in milliseconds
    private long validityInMs = 3600000; // 1h
}
