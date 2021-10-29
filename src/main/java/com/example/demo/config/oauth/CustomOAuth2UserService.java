package com.example.demo.config.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String MISSING_USER_INFO_ERROR_CODE = "missing_redirect_uri_access_code";

    private final OAuth2UserAttribute oauth2UserAttributeCustom;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, "userRequest cannot be null");

        String clientRegistrationId = userRequest.getClientRegistration().getRegistrationId();
        String resourceServerUri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        String accessToken = userRequest.getAccessToken().getTokenValue();
        log.info("clientRegistrationId : {}, resourceServerUri : {}, accessToken : {}", clientRegistrationId, resourceServerUri, accessToken);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (StringUtils.isEmpty(accessToken) || StringUtils.isEmpty(resourceServerUri)) {
            String description = String.format("Missing required redirect uri or access token for Client Registration: %s", clientRegistrationId);
            OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_INFO_ERROR_CODE, description, null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Bearer " + accessToken);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

            // 리소스 서버에 정보 요청
            String response = restTemplate.postForObject(resourceServerUri, request, String.class);
            Map<String, Object> attributes = oauth2UserAttributeCustom.getOAuth2UserAttributes(clientRegistrationId, response);
            OAuth2User user = new DefaultOAuth2User(authorities, attributes, OAuth2UserAttribute.USER_ID);
            return user;

        } catch (OAuth2AuthorizationException ex) {
            OAuth2Error oauth2Error = ex.getError();
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
