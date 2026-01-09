package me.shinsunyoung.projectweatherly.common;

import lombok.Data;

@Data
public class LocationRequest {
    private Double latitude;        // 위도 (GPS)
    private Double longitude;       // 경도 (GPS)
    private String ipAddress;       // IP주소
    private String regionCode;      // 지역 코드
    private String sido;            // 시/도
    private String sigungu;         // 시/군/구
    private String dong;            // 동/옵/면
}
