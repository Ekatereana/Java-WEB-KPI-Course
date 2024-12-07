package com.example.spacecatsmarket.config.noauth;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Data
@NoArgsConstructor
@Profile("no-auth")
@ConfigurationProperties(prefix = "auth")
public class NoAuthProperties {

    Map<String, Object> claims;

}
