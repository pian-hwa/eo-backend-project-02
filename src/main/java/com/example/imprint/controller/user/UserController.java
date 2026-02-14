package com.example.imprint.controller.user;

import com.example.imprint.domain.user.UserEntity;
import com.example.imprint.domain.user.UserJoinRequestDto;
import com.example.imprint.domain.user.UserLoginRequestDto;
import com.example.imprint.domain.user.UserResponseDto;
import com.example.imprint.security.user.CustomUserDetails;
import com.example.imprint.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> join(@RequestBody @Valid UserJoinRequestDto requestDto) {
        userService.registerUser(requestDto);
        return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserLoginRequestDto loginDto, HttpServletRequest request) {

        // 서비스에서 로그인 검증
        userService.login(loginDto.getEmail(), loginDto.getPassword());

        // 세션 생성 및 저장 (로그인 상태 유지)
        HttpSession session = request.getSession();
        session.setAttribute("userEmail", loginDto.getEmail());

        // response.status 가 200 으로 전송
        return ResponseEntity.ok("로그인이 성공적으로 완료되었습니다.");
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    // 담고있는 정보 반환
    @GetMapping("/user")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        // 인증 정보가 없는 경우 처리 (SecurityConfig에서 권한 설정을 했다면 사실 여기까지 못 들어옴)
        if (customUserDetails == null) {
            return ResponseEntity.status(401).build();
        }

        // CustomUserDetails 내부에 있는 UserEntity 꺼내기
        UserEntity user = customUserDetails.getUser();

        // DTO로 변환하여 반환
        return ResponseEntity.ok(UserResponseDto.fromEntity(user));
    }
}
