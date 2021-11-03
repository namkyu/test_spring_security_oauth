package com.example.demo.config.oauth.attr;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NaverResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("profile_image")
    private String profileImage;

    @JsonProperty("age")
    private String age;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("birthday")
    private String birthday;
}
