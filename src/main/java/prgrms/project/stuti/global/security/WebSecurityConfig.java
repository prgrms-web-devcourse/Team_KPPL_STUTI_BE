package prgrms.project.stuti.global.security;

import static org.springframework.http.HttpMethod.*;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.global.security.cache.repository.BlackListTokenRepository;
import prgrms.project.stuti.global.security.cache.repository.RefreshTokenRepository;
import prgrms.project.stuti.global.security.token.TokenService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final CustomOAuth2MemberService oAuth2UserService;
	private final OAuth2SuccessHandler successHandler;
	private final TokenService tokenService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BlackListTokenRepository blackListTokenRepository;

	@Value("${app.cors.allowed-origins}")
	private String [] domains;

	@Value("${app.oauth.domain}")
	private String domain;

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors().configurationSource(corsConfigurationSource()).and()
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.rememberMe(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(
				sessionManagement -> sessionManagement
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeRequests(
				authorizeRequests -> authorizeRequests
					.antMatchers("/**") // 개발 서버용
					.permitAll()

					.antMatchers(GET, "/redis/**")
					.hasAnyAuthority(MemberRole.ROLE_MEMBER.name())

					.anyRequest().authenticated()
			)
			.oauth2Login(
				oauth2Login -> oauth2Login
					.successHandler(successHandler)
					.failureHandler(failureHandler())
					.userInfoEndpoint()
					.userService(oAuth2UserService)
			)
			.addFilterBefore(
				new JwtAuthenticationFilter(tokenService, refreshTokenRepository, blackListTokenRepository),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	SimpleUrlAuthenticationFailureHandler failureHandler() {
		return new SimpleUrlAuthenticationFailureHandler(domain);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList(domains));
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}