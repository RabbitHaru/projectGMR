package me.shinsunyoung.projectweatherly.member.dto.social;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NaverUserInfoDto {

    @JsonProperty("resultcode")
    private String resultCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("response")
    private Response response;

    @Data
    public static class Response {
        private String id; // 네이버에서 제공하는 고유 ID
        private String email;
        private String name;
        private String nickname;
        private String profile_image;
        private String gender;
        private String age;
        private String birthday;
        private String birthyear;
        private String mobile;
    }
}