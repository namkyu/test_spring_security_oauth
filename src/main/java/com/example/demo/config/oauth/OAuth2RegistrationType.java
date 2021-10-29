package com.example.demo.config.oauth;


import lombok.Getter;

public enum OAuth2RegistrationType {

    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google");

    @Getter
    private String typeName;

    OAuth2RegistrationType(String typeName) {
        this.typeName = typeName;
    }
}
