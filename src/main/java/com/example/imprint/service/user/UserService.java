package com.example.imprint.service.user;

import com.example.imprint.domain.user.UserEntity;
import com.example.imprint.domain.user.UserJoinRequestDto;
import com.example.imprint.repository.user.EmailVerificationRepository;
import com.example.imprint.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository verificationRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(UserJoinRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        boolean isVerified = verificationRepository.existsByEmailAndIsVerifiedTrue(request.getEmail());
        if (!isVerified) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .name(request.getName())
                .build();

        user.activate();
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void login(String email, String password) {
        // 이메일 존재 확인
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 비밀번호 비교 (평문 패스워드 = 암호화된 패스워드)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}