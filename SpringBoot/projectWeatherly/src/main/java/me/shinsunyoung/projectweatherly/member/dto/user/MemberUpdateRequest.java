package me.shinsunyoung.projectweatherly.member.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class MemberUpdateRequest {

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String userPassword;

    @Size(max = 50, message = "이름은 50자 이내로 입력해주세요.")
    private String userName;

    @Size(max = 255, message = "프로필 이미지 URL은 255자 이내로 입력해주세요.")
    private String profileImage;

    @Size(max = 20, message = "권한은 20자 이내로 입력해주세요.")
    private String userRole;

    private Boolean isActive;
}