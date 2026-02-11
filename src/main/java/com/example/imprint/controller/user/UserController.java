package com.example.imprint.controller.user;

import com.example.imprint.domain.user.UserJoinRequestDto;
import com.example.imprint.domain.user.UserLoginRequestDto;
import com.example.imprint.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}