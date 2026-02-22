package com.example.imprint.controller.user;

import com.example.imprint.domain.ApiResponseDto;
import com.example.imprint.domain.admin.DashboardResponseDto;
import com.example.imprint.domain.message.MessageResponseDto;
import com.example.imprint.domain.user.UserRole;
import com.example.imprint.domain.user.UserStatus;
import com.example.imprint.service.admin.DashboardService;
import com.example.imprint.service.admin.AdminService;
import com.example.imprint.service.message.MessageService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
// 관리자만 해당 url로 접근 가능
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final DashboardService DashboardService;
    private final MessageService messageService;

    // 대시보드 메인 오버뷰 데이터 조회
    @GetMapping("/overview")
    public ResponseEntity<ApiResponseDto<DashboardResponseDto>> getDashboardOverview() {
        DashboardResponseDto overview = DashboardService.getOverview();
        return ResponseEntity.ok(ApiResponseDto.success(overview));
    }

    // 유저 직급 수정 ( ADMIN ↔ MANAGER ↔ USER )
    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponseDto<Void>> updateUserRole(
            @PathVariable Long userId,
            @RequestBody RoleUpdateRequest request) {

        adminService.updateUserRole(userId, request.getNewRole());
        return ResponseEntity.ok(ApiResponseDto.success("사용자의 권한이 성공적으로 변경되었습니다."));
    }

    // 유저 상태 수정 ( ACTIVE ↔ BANNED )
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponseDto<Void>> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody StatusUpdateRequest request) {

        adminService.updateUserStatus(userId, request.getNewStatus());
        return ResponseEntity.ok(ApiResponseDto.success("사용자의 상태가 성공적으로 변경되었습니다."));
    }

    // 특정 게시판의 매니저로 임명
    @PostMapping("/boards/{boardId}/managers/{userId}")
    public ResponseEntity<ApiResponseDto<Void>> assignManager(
            @PathVariable Long boardId,
            @PathVariable Long userId) {

        adminService.assignManager(userId, boardId);
        return ResponseEntity.ok(ApiResponseDto.success("해당 사용자가 게시판 매니저로 임명되었습니다."));
    }

    // 매니저 권한 회수 및 강등
    @DeleteMapping("/boards/{boardId}/managers/{userId}")
    public ResponseEntity<ApiResponseDto<Void>> dismissManager(
            @PathVariable Long boardId,
            @PathVariable Long userId) {

        adminService.dismissManager(userId, boardId);
        return ResponseEntity.ok(ApiResponseDto.success("해당 사용자의 매니저 권한이 회수되었습니다."));
    }

    // 관리자에게 온 쪽지 목록 조회
    @GetMapping("/supports")
    public ResponseEntity<ApiResponseDto<List<MessageResponseDto>>> getSupports() {
        List<MessageResponseDto> supports = messageService.getAdminSupports();
        return ResponseEntity.ok(ApiResponseDto.success(supports));
    }


    // --- 내부 데이터 전달용 DTO( body에 담는 용도 ) ---

    @Getter
    @NoArgsConstructor
    public static class StatusUpdateRequest {
        private UserStatus newStatus;
    }

    @Getter
    @NoArgsConstructor
    public static class RoleUpdateRequest {
        private UserRole newRole;
    }

    // --- 대시보드 응답용 DTO ---
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DashboardOverviewResponse {
        // 총 가입자 수
        private long totalUserCount;
        // 금일 신규 가입자
        private long todaySignupCount;
        // 금일 게시글 수
//        private long todayPostCount;
        // 금일 댓글 수
//        private long todayCommentCount;
        // 미확인 신고 건수
        private long pendingReportCount;
    }
}