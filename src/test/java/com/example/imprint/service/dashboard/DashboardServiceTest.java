package com.example.imprint.service.dashboard;

import com.example.imprint.domain.admin.DashboardResponseDto;
import com.example.imprint.domain.user.UserStatus;
import com.example.imprint.repository.message.report.ReportRepository;
import com.example.imprint.repository.user.UserRepository;
import com.example.imprint.service.admin.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @InjectMocks
    private DashboardService dashboardService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportRepository reportRepository;

    @Test
    @DisplayName("대시보드 오버뷰 통계 데이터가 정확하게 조회되어야 한다")
    void getOverviewTest() {
        // given
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByStatus(UserStatus.ACTIVE)).thenReturn(90L);
        when(userRepository.countByStatus(UserStatus.BANNED)).thenReturn(10L);
        when(userRepository.countByCreatedAtAfter(any(LocalDateTime.class))).thenReturn(5L);
        when(reportRepository.count()).thenReturn(3L);

        // when
        DashboardResponseDto result = dashboardService.getOverview();

        // then
        assertThat(result.getTotalUserCount()).isEqualTo(100L);
        assertThat(result.getActiveUserCount()).isEqualTo(90L);
        assertThat(result.getBannedUserCount()).isEqualTo(10L);
        assertThat(result.getTodaySignupCount()).isEqualTo(5L);
        assertThat(result.getPendingReportCount()).isEqualTo(3L);
    }
}