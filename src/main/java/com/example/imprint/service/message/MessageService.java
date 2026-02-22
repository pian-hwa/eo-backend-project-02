package com.example.imprint.service.message;

import com.example.imprint.domain.message.MessageEntity;
import com.example.imprint.domain.message.MessageResponseDto;
import com.example.imprint.domain.user.UserEntity;
import com.example.imprint.repository.message.MessageRepository;
import com.example.imprint.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // 쪽지 보내기
    @Transactional
    public void sendMessage(String senderEmail, String receiverNickname, String content) {
        // 보낸 사람과 받는 사람 엔티티 조회
        UserEntity sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람 정보를 찾을 수 없습니다."));

        UserEntity receiver = userRepository.findByNickname(receiverNickname)
                .orElseThrow(() -> new IllegalArgumentException("받는 사람(" + receiverNickname + ")을 찾을 수 없습니다."));

        // 쪽지 생성 및 저장
        MessageEntity message = MessageEntity.builder()
                .content(content)
                .sender(sender)
                .receiver(receiver)
                .build();

        messageRepository.save(message);
    }

    // 쪽지 읽음 처리 (상태 변환)
    @Transactional
    public MessageResponseDto readMessage(Long messageId, String userEmail) {
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("쪽지를 찾을 수 없습니다."));

        // 본인에게 온 쪽지인지 검증
        if (!message.getReceiver().getEmail().equals(userEmail) && !message.getSender().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("쪽지를 읽을 권한이 없습니다.");
        }

        // 받은 사람이 읽었을 때만 '읽음' 표시와 '시간' 기록
        if (message.getReceiver().getEmail().equals(userEmail)) {
            message.read();
        }

        // 엔티티를 DTO로 변환해서 컨트롤러에게 전달
        return MessageResponseDto.from(message);
    }

    // 받은 쪽지함 전체 조회
    // 받은 쪽지함 탭 클릭: GET /message/list 호출
    public List<MessageResponseDto> getReceivedMessages(String email) {
        // 현재 로그인한 유저 엔티티 조회
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 받은 쪽지 리스트 가져오기 (삭제 안 된 것만)
        List<MessageEntity> messages = messageRepository.findAllByReceiverAndDeletedByReceiverFalseOrderByCreatedAtDesc(user);

        // Entity 리스트를 DTO 리스트로 변환해서 반환
        return messages.stream()
                .map(MessageResponseDto::from)
                .collect(Collectors.toList());
    }


    // 보낸 쪽지함 전체 조회
    // 보낸 쪽지함 탭 클릭: GET /message/sent 호출
    public List<MessageResponseDto> getSentMessages(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이번에는 Sender가 "나" 인 쪽지를 조회
        return messageRepository.findAllBySenderAndDeletedBySenderFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(MessageResponseDto::from)
                .collect(Collectors.toList());
    }

    // 쪽지 삭제 (보낸 사람 기준)
    @Transactional
    public void deleteBySender(Long messageId, String email) {
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("쪽지를 찾을 수 없습니다."));

        if (!message.getSender().getEmail().equals(email)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        // Entity의 deletedBySender = true
        message.deleteBySender();
    }

    // 쪽지 삭제 (받는 사람 기준)
    @Transactional
    public void deleteByReceiver(Long messageId, String email) {
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("쪽지를 찾을 수 없습니다."));

        if (!message.getReceiver().getEmail().equals(email)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        // Entity의 deletedByReceiver = true
        message.deleteByReceiver();
    }

    // 관리자 대시보드용 (운영진에게 온 모든 문의 메시지 조회)
    public List<MessageResponseDto> getAdminSupports() {
        return messageRepository.findAdminSupports().stream()
                .map(entity -> MessageResponseDto.builder()
                        .id(entity.getId())
                        .content(entity.getContent())
                        .senderNickname(entity.getSender().getNickname())
                        .createdAt(entity.getCreatedAt())
                        .build())
                .toList();
    }
}