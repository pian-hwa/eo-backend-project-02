package com.example.imprint.service.admin;

import com.example.imprint.domain.admin.DashboardResponseDto;
import com.example.imprint.domain.user.UserStatus;
import com.example.imprint.repository.PostRepository;
import com.example.imprint.repository.message.MessageRepository;
import com.example.imprint.repository.message.report.ReportRepository;
import com.example.imprint.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final UserRepository userRepository;
//    private final PostRepository postRepository;
//    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final MessageRepository messageRepository;

    public DashboardResponseDto getOverview() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        return DashboardResponseDto.builder()
                // 총 사용자
                .totalUserCount(userRepository.count())
                // 금일 가입자
                .todaySignupCount(userRepository.countByCreatedAtAfter(startOfDay))
                // 사용자(ACTIVE)
                .activeUserCount(userRepository.countByStatus(UserStatus.ACTIVE))
                // 차단유저(BANNED)
                .bannedUserCount(userRepository.countByStatus(UserStatus.BANNED))
                // .todayPostCount(postRepository.countByCreatedAtAfter(startOfDay))
                // .todayCommentCount(commentRepository.countByCreatedAtAfter(startOfDay))
                .pendingReportCount(reportRepository.count())
//                .pendingSupportCount(messageRepository.countPendingInquiries())
                .build();
    }


}
