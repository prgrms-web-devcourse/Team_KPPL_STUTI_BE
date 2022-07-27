package prgrms.project.stuti.global.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.jwt")
@Component
@Getter
@Setter
public class JwtProperties {

    private String header;
    private String issuer;
    private String tokenSecret;
    private long tokenExpiry;
    private long refreshTokenExpiry;
}
