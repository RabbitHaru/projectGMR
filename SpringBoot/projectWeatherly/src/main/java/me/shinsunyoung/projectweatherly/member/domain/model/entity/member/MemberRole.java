package me.shinsunyoung.projectweatherly.member.domain.model.entity.member;

import lombok.Getter;

@Getter
public enum MemberRole {
    USER("일반 사용자"),
    REPORTER("리포터"),
    ADMIN("관리자");

    private final String description;

    MemberRole(String description) {
        this.description = description;
    }

    public static MemberRole fromString(String role) {
        for (MemberRole memberRole : MemberRole.values()) {
            if (memberRole.name().equalsIgnoreCase(role)) {
                return memberRole;
            }
        }
        return USER; // 기본값
    }
}