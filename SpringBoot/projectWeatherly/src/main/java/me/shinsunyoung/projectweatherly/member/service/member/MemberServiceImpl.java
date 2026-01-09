package me.shinsunyoung.projectweatherly.member.service.member;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.projectweatherly.member.domain.model.entity.member.Member;
import me.shinsunyoung.projectweatherly.member.domain.model.entity.member.MemberRole;
import me.shinsunyoung.projectweatherly.member.repository.member.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.AuthProvider;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MemberResponse register(MemberJoinRequest request) {
        // 이메일 중복 체크
        if (memberRepository.existsByUserEmail(request.getUserEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 소셜 로그인 사용자가 아닌 경우 비밀번호 검증
        String encryptedPassword = null;
        if (request.getAuthProvider().equalsIgnoreCase("local")) {
            if (request.getUserPassword() == null || request.getUserPassword().isEmpty()) {
                throw new RuntimeException("로컬 회원가입 시 비밀번호는 필수입니다.");
            }
            encryptedPassword = passwordEncoder.encode(request.getUserPassword());
        }

        // 회원 엔티티 생성
        Member member = Member.builder()
                .userEmail(request.getUserEmail())
                .userPassword(encryptedPassword)
                .userName(request.getUserName())
                .profileImage(request.getProfileImage())
                .userRole(MemberRole.fromString(request.getUserRole()))
                .authProvider(AuthProvider.fromString(request.getAuthProvider()))
                .providerId(request.getProviderId())
                .isActive(request.getIsActive())
                .build();

        // 저장
        Member savedMember = memberRepository.save(member);
        log.info("회원 가입 완료: {}", savedMember.getUserEmail());

        return toResponse(savedMember);
    }

    @Override
    public MemberResponse getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        return toResponse(member);
    }

    @Override
    public MemberResponse getMemberByEmail(String email) {
        Member member = memberRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        return toResponse(member);
    }

    @Override
    @Transactional
    public MemberResponse updateMember(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        // 이름 업데이트
        if (request.getUserName() != null) {
            member.setUserName(request.getUserName());
        }

        // 프로필 이미지 업데이트
        if (request.getProfileImage() != null) {
            member.setProfileImage(request.getProfileImage());
        }

        // 권한 업데이트 (관리자만 가능하도록 서비스 레이어에서 제한 필요)
        if (request.getUserRole() != null) {
            member.setUserRole(MemberRole.fromString(request.getUserRole()));
        }

        Member updatedMember = memberRepository.save(member);
        log.info("회원 정보 업데이트 완료: {}", updatedMember.getUserEmail());

        return toResponse(updatedMember);
    }

    @Override
    @Transactional
    public void changePassword(Long memberId, String currentPassword, String newPassword) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        // 로컬 회원인지 확인
        if (member.getAuthProvider() != AuthProvider.LOCAL) {
            throw new RuntimeException("소셜 로그인 회원은 비밀번호를 변경할 수 없습니다.");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, member.getUserPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 및 저장
        String encryptedNewPassword = passwordEncoder.encode(newPassword);
        member.changePassword(encryptedNewPassword);

        memberRepository.save(member);
        log.info("비밀번호 변경 완료: {}", member.getUserEmail());
    }

    @Override
    @Transactional
    public MemberResponse updateProfileImage(Long memberId, String imageUrl) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        member.setProfileImage(imageUrl);
        Member updatedMember = memberRepository.save(member);

        log.info("프로필 이미지 업데이트 완료: {}", updatedMember.getUserEmail());
        return toResponse(updatedMember);
    }

    @Override
    @Transactional
    public void deactivateMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        member.deactivate();
        memberRepository.save(member);
        log.info("회원 비활성화 완료: {}", member.getUserEmail());
    }

    @Override
    @Transactional
    public void activateMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        member.activate();
        memberRepository.save(member);
        log.info("회원 활성화 완료: {}", member.getUserEmail());
    }

    @Override
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MemberResponse> getMembersByRole(String role) {
        return memberRepository.findActiveMembersByRole(role).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Member getMemberEntityById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
    }

    @Override
    public Member getMemberEntityByEmail(String email) {
        return memberRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
    }

    // Member 엔티티를 MemberResponse DTO로 변환
    private MemberResponse toResponse(Member member) {
        return MemberResponse.builder()
                .userId(member.getUserId())
                .userEmail(member.getUserEmail())
                .userName(member.getUserName())
                .profileImage(member.getProfileImage())
                .userRole(member.getUserRole().name())
                .authProvider(member.getAuthProvider().name())
                .isActive(member.getIsActive())
                .lastLoginAt(member.getLastLoginAt())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}