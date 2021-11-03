package com.example.demo.config;

import com.example.demo.common.JwtProperties;
import com.example.demo.config.oauth.CustomOAuth2SuccessHandler;
import com.example.demo.config.oauth.CustomOAuth2UserService;
import com.example.demo.config.oauth.OAuth2UserAttribute;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.access.AccessDeniedHandler;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String TARGET_URL = "/index.html";
    public static final String ERROR_URL = "/denied";

    private final TokenHelper tokenHelper;
    private final UserDetailsServiceImp userDetailsService;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public CustomOAuth2SuccessHandler customOAuth2SuccessHandler() {
        return new CustomOAuth2SuccessHandler(tokenHelper, TARGET_URL, jwtProperties);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenAuthenticationFilter jwtAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenHelper, jwtProperties);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        CustomAccessDeniedHandler customAccessDeniedHandler = new CustomAccessDeniedHandler(ERROR_URL);
        return customAccessDeniedHandler;
    }

    // 스프링 시큐리티가 사용자를 인증하는 방법이 담긴 객체
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
        return new CustomOAuth2UserService(new OAuth2UserAttribute(objectMapper));
    }

    // 스프링 시큐리티를 무시하게 하는 URL 규칙
    // Security 필터체인을 적용하고 싶지 않은 리소스에 대해 설정을 할수있도록 ignore() 를 제공
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/css/**"
                , "/fonts/**"
                , "/images/**"
                , "/img/**"
                , "/js/**"
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationFilter(), OAuth2AuthorizationRequestRedirectFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                    .antMatchers("/index.html").permitAll()
                    .antMatchers("/logout.html").permitAll()
                    .antMatchers("/oauth2Login.html").permitAll()
                    .antMatchers("/denied").permitAll()
                    .antMatchers("/login/**").permitAll()
                    .antMatchers("/admin/**").hasAnyRole("ADMIN")
                    .antMatchers("/user/**").authenticated()
                    .anyRequest().authenticated()

                .and()
                .exceptionHandling()
                    .accessDeniedHandler(accessDeniedHandler())

                .and()
                .oauth2Login() // oauth2 로그인 관련 처리 설정
                    .loginPage("/oauth2Login.html")
                        .authorizationEndpoint()
                            .baseUri("/login/oauth2/authorization")
                            .and()
                        .redirectionEndpoint()
                            .baseUri("/login/oauth2/callback/*")
                            .and()
                    .userInfoEndpoint().userService(customOAuth2UserService()) // oauth2 인증 과정에서 authentication 생성에 필요한 OAuth2User 를 반환한다.
                    .and()
                    .successHandler(customOAuth2SuccessHandler()) // 인증 성공

                .and()
                .logout()
                    .logoutUrl("/logout")
                    .deleteCookies(jwtProperties.getCookieName())
                    .logoutSuccessUrl("/logout.html")

                .and()
                .csrf().disable();
    }
}
