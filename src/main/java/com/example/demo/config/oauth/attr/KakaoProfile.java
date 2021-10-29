package com.example.demo.config.oauth.attr;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoProfile {

    @JsonProperty("nickname")
    private String nickname;
}
