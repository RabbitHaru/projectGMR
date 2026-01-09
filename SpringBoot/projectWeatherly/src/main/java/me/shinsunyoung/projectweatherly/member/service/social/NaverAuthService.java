package me.shinsunyoung.projectweatherly.member.service.social;


import lombok.extern.slf4j.Slf4j;
import me.shinsunyoung.projectweatherly.member.dto.social.NaverUserInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class NaverAuthService {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    @Value("${naver.provider.naver.token-uri}")
    private String tokenUri;

    @Value("${naver.provider.naver.user-info-uri}")
    private String userInfoUri;

    public String getAccessToken(String code, String state) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        }

        throw new RuntimeException("네이버 액세스 토큰 요청 실패");
    }

    public NaverUserInfoDto getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<NaverUserInfoDto> response = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                entity,
                NaverUserInfoDto.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }

        throw new RuntimeException("네이버 사용자 정보 요청 실패");
    }

    public Map<String, String> processNaverLogin(String code, String state) {
        // 1. 액세스 토큰 획득
        String accessToken = getAccessToken(code, state);

        // 2. 사용자 정보 조회
        NaverUserInfoDto naverUserInfo = getUserInfo(accessToken);

        // 3. 응답 데이터 구성
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("providerId", naverUserInfo.getResponse().getId());
        userInfo.put("email", naverUserInfo.getResponse().getEmail());
        userInfo.put("name", naverUserInfo.getResponse().getName());
        userInfo.put("profileImage", naverUserInfo.getResponse().getProfile_image());
        userInfo.put("authProvider", "naver");

        return userInfo;
    }
}