package com.example.demo.config.oauth;

import com.example.demo.config.oauth.attr.KakaoAccount;
import com.example.demo.config.oauth.attr.KakaoAttr;
import com.example.demo.config.oauth.attr.NaverAttr;
import com.example.demo.config.oauth.attr.NaverResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class OAuth2UserAttribute {

    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String USER_EMAIL = "userEmail";

    private final ObjectMapper objectMapper;

    /**
     * @param registrationId
     * @param response
     * @return
     * @throws JsonProcessingException
     */
    public Map<String, Object> getOAuth2UserAttributes(String registrationId, String response) throws JsonProcessingException {
        Map<String, Object> attributes = new HashMap<>();

        if (OAuth2RegistrationType.KAKAO.getTypeName().equals(registrationId)) {
            KakaoAttr kakaoAttr = objectMapper.readValue(response, KakaoAttr.class);
            KakaoAccount kakaoAccount = kakaoAttr.getKakaoAccount();

            String id = kakaoAttr.getId();
            String email = kakaoAccount.getEmail();
            String nickname = kakaoAccount.getKakaoProfile().getNickname();
            attributes.put(USER_ID, id);
            attributes.put(USER_NAME, nickname);
            attributes.put(USER_EMAIL, email);
        }
        else if (OAuth2RegistrationType.NAVER.getTypeName().equals(registrationId)) {
            NaverAttr naverAttr = objectMapper.readValue(response, NaverAttr.class);
            NaverResponse naverResponse = naverAttr.getResponse();

            String id = naverResponse.getId();
            String email = naverResponse.getEmail();
            attributes.put(USER_ID, id);
            attributes.put(USER_NAME, email);
            attributes.put(USER_EMAIL, email);
        }

        return attributes;
    }


}
