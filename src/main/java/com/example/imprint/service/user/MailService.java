package com.example.imprint.service.user;

import com.example.imprint.domain.user.EmailVerification;
import com.example.imprint.repository.user.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository verificationRepository;

    @Transactional
    public void sendCodeToEmail(String email) {
        String cleanEmail = email.trim().replace("\"", "");

        System.out.println("발송 시도 이메일: [" + cleanEmail + "]");

        String authCode = createCode();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(cleanEmail);
        message.setFrom("ImPrint");
        message.setSubject("[Imprint] 회원가입 인증번호입니다.");
        message.setText("인증번호: " + authCode + "\n5분 이내에 입력해주세요.");

        mailSender.send(message);

        EmailVerification verification = EmailVerification.builder()
                .email(cleanEmail)
                .authCode(authCode)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        verificationRepository.save(verification);
    }

    @Transactional
    public boolean verifyCode(String email, String code) {
        EmailVerification verification = verificationRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 요청 기록이 없습니다."));

        // 시간 만료 확인 및 코드 일치 확인
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("인증 시간이 만료되었습니다.");
        }
        if (!verification.getAuthCode().equals(code)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        // isVerified = true 상태 변경
        verification.verify();
        return true;
    }

    private String createCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }
}
