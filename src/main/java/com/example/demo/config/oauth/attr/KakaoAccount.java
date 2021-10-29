package com.example.demo.config.oauth.attr;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoAccount {

    @JsonProperty("profile_nickname_needs_agreement")
    private Boolean profileNicknameNeedsAgreement;

    @JsonProperty("profile")
    private KakaoProfile kakaoProfile;

    @JsonProperty("has_email")
    private Boolean hasEmail;

    @JsonProperty("email_needs_agreement")
    private Boolean emailNeedsAgreement;

    @JsonProperty("is_email_valid")
    private Boolean isEmailValid;

    @JsonProperty("is_email_verified")
    private Boolean isEmailVerified;

    @JsonProperty("email")
    private String email;

    @JsonProperty("has_gender")
    private Boolean hasGender;

    @JsonProperty("gender_needs_agreement")
    private Boolean genderNeedsAgreement;

    @JsonProperty("gender")
    private String gender;
}
