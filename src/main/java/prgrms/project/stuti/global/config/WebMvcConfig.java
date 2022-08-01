package prgrms.project.stuti.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Value("${app.cors.allowed-origins}")
	private String[] allowedOrigins;

	@Value("${app.cors.allowed-methods}")
	private String[] allowedMethods;

	@Value("${app.cors.allowed-headers}")
	private String[] allowedHeaders;

	@Value("${app.cors.max-age}")
	private long maxAgeSecond;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins(allowedOrigins)
			.allowedMethods(allowedMethods)
			.allowedHeaders(allowedHeaders)
			.allowCredentials(true)
			.maxAge(maxAgeSecond);
	}
}