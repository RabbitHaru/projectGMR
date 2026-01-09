package me.shinsunyoung.projectweatherly.member.repository.member;


import me.shinsunyoung.projectweatherly.member.domain.model.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.security.AuthProvider;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 회원 찾기
    Optional<Member> findByUserEmail(String userEmail);

    // 이메일 존재 여부 확인
    boolean existsByUserEmail(String userEmail);

    // 소셜 로그인 제공자와 제공자 ID로 회원 찾기
    Optional<Member> findByAuthProviderAndProviderId(AuthProvider authProvider, String providerId);

    // 이메일과 활성 상태로 회원 찾기
    Optional<Member> findByUserEmailAndIsActiveTrue(String userEmail);

    // 특정 권한을 가진 회원들 찾기
    @Query("SELECT m FROM Member m WHERE m.userRole = :role AND m.isActive = true")
    java.util.List<Member> findActiveMembersByRole(@Param("role") String role);

    // 비활성화된 회원들 찾기
    java.util.List<Member> findByIsActiveFalse();

    // 마지막 로그인 이후 일정 기간이 지난 회원들 찾기
    @Query("SELECT m FROM Member m WHERE m.lastLoginAt < :date AND m.isActive = true")
    java.util.List<Member> findInactiveMembersSince(@Param("date") java.time.LocalDateTime date);
}