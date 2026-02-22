package com.example.imprint.service.message;

import com.example.imprint.domain.message.MessageEntity;
import com.example.imprint.domain.message.MessageResponseDto;
import com.example.imprint.domain.user.UserEntity;
import com.example.imprint.domain.user.UserRole;
import com.example.imprint.repository.message.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SupportServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MessageRepository messageRepository;

    private UserEntity admin;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        admin = UserEntity.builder()
                .email("admin@test.com")
                .role(UserRole.ADMIN)
                .build();

        user = UserEntity.builder()
                .email("user@test.com")
                .nickname("문의자")
                .build();
    }

    @Test
    @DisplayName("관리자 전용 문의 목록(Supports) 조회 테스트")
    void getAdminSupportsTest() {
        // given
        MessageEntity msg = MessageEntity.builder()
                .content("관리자 문의 내용")
                .sender(user)
                .receiver(admin)
                .build();
        when(messageRepository.findAdminSupports()).thenReturn(List.of(msg));

        // when
        List<MessageResponseDto> result = messageService.getAdminSupports();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSenderNickname()).isEqualTo("문의자");
    }

    @Test
    @DisplayName("쪽지 읽음 처리 및 상태 전환 테스트")
    void readMessageStatusTest() {
        // given
        MessageEntity msg = MessageEntity.builder()
                .id(1L)
                .receiver(admin)
                .sender(user)
                .isRead(false)
                .build();
        when(messageRepository.findById(1L)).thenReturn(Optional.of(msg));

        // when: 수신자(Admin)가 읽었을 때
        messageService.readMessage(1L, "admin@test.com");

        // then
        assertThat(msg.isRead()).isTrue();
        assertThat(msg.getReadAt()).isNotNull();
    }

    @Test
    @DisplayName("권한 없는 사용자의 접근 차단 테스트")
    void readMessageAccessDeniedTest() {
        // given
        MessageEntity msg = MessageEntity.builder()
                .id(1L)
                .receiver(admin)
                .sender(user)
                .build();
        when(messageRepository.findById(1L)).thenReturn(Optional.of(msg));

        // when & then: 제3자가 읽으려 할 때
        assertThatThrownBy(() -> messageService.readMessage(1L, "intruder@test.com"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("읽지 않은 문의(Pending) 카운트 조회 테스트")
    void countPendingInquiriesTest() {
        // given
        when(messageRepository.countPendingSupports()).thenReturn(5L);

        // when
        long count = messageRepository.countPendingSupports();

        // then
        assertThat(count).isEqualTo(5L);
    }
}
