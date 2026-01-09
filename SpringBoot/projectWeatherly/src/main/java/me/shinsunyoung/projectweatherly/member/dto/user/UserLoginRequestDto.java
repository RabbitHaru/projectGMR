package me.shinsunyoung.projectweatherly.member.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDto {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String userEmail;

    private String userPassword; // 소셜 로그인인 경우 null
    private String authProvider; // local, kakao, naver
    private String providerId; // 소셜 로그인 ID
}