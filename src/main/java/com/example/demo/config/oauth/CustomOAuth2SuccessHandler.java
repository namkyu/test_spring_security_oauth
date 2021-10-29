package com.example.demo.config.oauth;

import com.example.demo.common.JwtProperties;
import com.example.demo.config.TokenHelper;
import com.example.demo.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenHelper tokenHelper;
    private final String targetUrl;
    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
        String name = user.getName();
        String[] authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);

        String jwt = tokenHelper.generateToken(name, authorities);

        String cookieName = jwtProperties.getCookieName();
        Integer expiresIn = jwtProperties.getExpiresIn();
        Cookie cookie = CookieUtils.generateJwtHttpOnlyCookie(cookieName, jwt, expiresIn);
        response.addCookie(cookie);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
