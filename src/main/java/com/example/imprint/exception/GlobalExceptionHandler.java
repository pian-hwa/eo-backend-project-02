package com.example.imprint.exception;

import com.example.imprint.domain.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * 비즈니스 로직 예외 (400 Bad Request)
     * 예: 비밀번호 불일치, 가입되지 않은 이메일, 존재하지 않는 닉네임 등
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Business Logic Error: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.fail(e.getMessage()));
    }

    /*
     * 권한 부족 예외 (403 Forbidden)
     * 예: 남의 쪽지 읽기 시도, 관리자 페이지 접근 등
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access Denied: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponseDto.fail("접근 권한이 없습니다."));
    }

    /*
     * 인증 실패 예외 (401 Unauthorized)
     * 예: 시큐리티 인증 과정 중 아이디/비번 틀림
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("Authentication Failed: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDto.fail("로그인 정보가 올바르지 않습니다."));
    }

    /*
     * 시스템 전체 예외 (500 Internal Server Error)
     * 예상치 못한 런타임 에러 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGeneralException(Exception e) {
        // 서버 로그에는 상세 에러 남김
        log.error("Unexpected System Error: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.fail("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}