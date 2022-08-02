package prgrms.project.stuti.global.security;

import static org.springframework.http.HttpMethod.*;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import prgrms.project.stuti.domain.member.model.MemberRole;
import prgrms.project.stuti.global.cache.repository.BlackListTokenRepository;
import prgrms.project.stuti.global.cache.repository.RefreshTokenRepository;
import prgrms.project.stuti.global.token.TokenService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final CustomOAuth2MemberService oAuth2UserService;
	private final OAuth2SuccessHandler successHandler;
	private final TokenService tokenService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BlackListTokenRepository blackListTokenRepository;

	@Value("${app.oauth.domain}")
	private String domain;

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, e) -> {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.sendRedirect(domain);
		};
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return (request, response, e) -> {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.sendRedirect(domain);
		};
	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors().and()
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
					.antMatchers("/api/**") // 개발 서버용
					.permitAll()

					.antMatchers(GET, "/redis/**")
					.hasAnyAuthority(MemberRole.ROLE_MEMBER.name())

					.anyRequest().authenticated()
			)
			.oauth2Login(
				oauth2Login -> oauth2Login
					.successHandler(successHandler)
					.userInfoEndpoint()
					.userService(oAuth2UserService)
			)
			.addFilterBefore(
				new JwtAuthenticationFilter(tokenService, refreshTokenRepository, blackListTokenRepository),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
