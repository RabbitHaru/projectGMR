package me.shinsunyoung.projectweatherly.member.dto.social;

import lombok.Data;

@Data
public class SocialLoginRequestDto {
    private String accessToken;
    private String provider; // naver, kakao 등
}