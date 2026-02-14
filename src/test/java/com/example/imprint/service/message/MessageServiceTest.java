package com.example.imprint.service.message;

import com.example.imprint.domain.message.MessageEntity;
import com.example.imprint.domain.user.UserEntity;
import com.example.imprint.domain.user.UserRole;
import com.example.imprint.repository.message.MessageRepository;
import com.example.imprint.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MessageServiceTest {

    @Autowired MessageService messageService;
    @Autowired UserRepository userRepository;
    @Autowired MessageRepository messageRepository;

    private UserEntity sender;
    private UserEntity receiver;

    @BeforeEach
    void setUp() {
        // 테스트용 발신자 생성
        sender = UserEntity.builder()
                .email("sender@test.com")
                .password("1234")
                .nickname("보내는사람")
                .name("강감찬")
                .status("ACTIVE")
                .role(UserRole.USER)
                .build();
        userRepository.save(sender);

        // 테스트용 수신자 생성
        receiver = UserEntity.builder()
                .email("receiver@test.com")
                .password("1234")
                .nickname("받는사람")
                .name("이순신")
                .status("ACTIVE")
                .role(UserRole.USER)
                .build();
        userRepository.save(receiver);
    }

    @Test
    @DisplayName("쪽지 발송 성공 테스트")
    void sendMessageTest() {
        // Given
        String content = "안녕하세요, 차 구경 좀 시켜주세요!";

        // When
        messageService.sendMessage(sender.getEmail(), receiver.getNickname(), content);

        // Then
        List<MessageEntity> messages = messageRepository.findAll();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getContent()).isEqualTo(content);
        assertThat(messages.get(0).getSender().getNickname()).isEqualTo("보내는사람");
        assertThat(messages.get(0).getReceiver().getNickname()).isEqualTo("받는사람");
        assertThat(messages.get(0).isRead()).isFalse();
    }
}