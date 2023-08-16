package com.ssafy.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.backend.domain.user.repository.UserRepository;
import com.ssafy.backend.global.jwt.filter.JwtAuthenticationProcessingFilter;
import com.ssafy.backend.global.jwt.service.JwtService;
import com.ssafy.backend.oauth2.handler.OAuth2LoginFailureHandler;
import com.ssafy.backend.oauth2.handler.OAuth2LoginSuccessHandler;
import com.ssafy.backend.oauth2.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

/**
 * 인증은 CustomJsonUsernamePasswordAuthenticationFilter에서 authenticate()로 인증된 사용자로 처리
 * JwtAuthenticationProcessingFilter는 AccessToken, RefreshToken 재발급
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	//    private final LoginService loginService;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
	private final CustomOAuth2UserService customOAuth2UserService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().configurationSource(corsConfigurationSource());
		http
			.formLogin().disable() // 기본 제공되는 FormLogin 사용 X
			.httpBasic().disable() // httpBasic 사용 X
			.csrf().disable() // csrf 보안 사용 X -> 서버에 인증 정보를 요청하지 않고 JWT로 할거니까
			.headers().frameOptions().disable() // h2 쓰려고 disable
			.and()

			// 세션 사용하지 않으므로 STATELESS로 설정
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()

			//== URL별 권한 관리 옵션 ==//
			.authorizeRequests()

			// 아이콘, css, js 관련
			// 기본 페이지, css, image, js 하위 폴더에 있는 자료들은 인증없이 모두 접근 가능, h2-console에 접근 가능
			.antMatchers("/", "/v3/api-docs/**", "/swagger-ui/**", "/css/**", "/images/**", "/js/**", "/favicon.ico",
				"/h2-console/**").permitAll()
			.antMatchers(HttpMethod.OPTIONS, "/**/*").permitAll() // options 무시
			.antMatchers("/couple-certification", "/auto-login", "/invitation/share/*").permitAll() // 커플 인증 요청 접근 가능
			.anyRequest().authenticated() // 위의 경로 이외에는 모두 인증된 사용자만 접근 가능
			.and()
			.logout()
			.logoutSuccessUrl("/")
			.and()
			//== 소셜 로그인 설정 ==//
			.oauth2Login()
			.successHandler(oAuth2LoginSuccessHandler) // 동의하고 계속하기를 눌렀을 때 Handler 설정
			.failureHandler(oAuth2LoginFailureHandler) // 소셜 로그인 실패 시 핸들러 설정
			.userInfoEndpoint().userService(customOAuth2UserService); // customUserService 설정

		// 원래 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작
		// 따라서, LogoutFilter 이후에 우리가 만든 필터 동작하도록 설정
		// 순서 : LogoutFilter -> JwtAuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
		//        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
		http.addFilterAfter(jwtAuthenticationProcessingFilter(), LogoutFilter.class);
		//        http.addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		//허용할 url 설정
//		configuration.addAllowedOrigin("http://43.200.254.50");
		configuration.addAllowedOrigin("https://i9e104.p.ssafy.io");
		//허용할 헤더 설정
		configuration.addAllowedHeader("*");
		//허용할 http method
		configuration.addAllowedMethod("*");
		// 클라이언트가 접근 할 수 있는 서버 응답 헤더
		configuration.addExposedHeader("Authorization");
		configuration.addExposedHeader("Authorization_refresh");
		//사용자 자격 증명이 지원되는지 여부
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	//    /**
	//     * AuthenticationManager 설정 후 등록
	//     * PasswordEncoder를 사용하는 AuthenticationProvider 지정 (PasswordEncoder는 위에서 등록한 PasswordEncoder 사용)
	//     * FormLogin(기존 스프링 시큐리티 로그인)과 동일하게 DaoAuthenticationProvider 사용
	//     * UserDetailsService는 커스텀 LoginService로 등록
	//     * 또한, FormLogin과 동일하게 AuthenticationManager로는 구현체인 ProviderManager 사용(return ProviderManager)
	//     */
	//    @Bean
	//    public AuthenticationManager authenticationManager() {
	//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	//        provider.setPasswordEncoder(passwordEncoder());
	//        provider.setUserDetailsService(loginService);
	//        return new ProviderManager(provider);
	//    }
	//
	//    /**
	//     * 로그인 성공 시 호출되는 LoginSuccessJWTProviderHandler 빈 등록
	//     */
	//    @Bean
	//    public LoginSuccessHandler loginSuccessHandler() {
	//        return new LoginSuccessHandler(jwtService, userRepository);
	//    }
	//
	//    /**
	//     * 로그인 실패 시 호출되는 LoginFailureHandler 빈 등록
	//     */
	//    @Bean
	//    public LoginFailureHandler loginFailureHandler() {
	//        return new LoginFailureHandler();
	//    }
	//
	//    /**
	//     * CustomJsonUsernamePasswordAuthenticationFilter 빈 등록
	//     * 커스텀 필터를 사용하기 위해 만든 커스텀 필터를 Bean으로 등록
	//     * setAuthenticationManager(authenticationManager())로 위에서 등록한 AuthenticationManager(ProviderManager) 설정
	//     * 로그인 성공 시 호출할 handler, 실패 시 호출할 handler로 위에서 등록한 handler 설정
	//     */
	//    @Bean
	//    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
	//        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
	//                = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
	//        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
	//        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
	//        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
	//        return customJsonUsernamePasswordLoginFilter;
	//    }

	@Bean
	public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
		return new JwtAuthenticationProcessingFilter(jwtService, userRepository);
	}
}