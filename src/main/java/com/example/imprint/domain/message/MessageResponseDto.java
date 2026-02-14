package com.example.imprint.domain.message;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {
    private Long id;
    private String content;
    private String senderNickname;   // 보낸 사람 닉네임 (화면 표시용)
    private String receiverNickname; // 받는 사람 닉네임
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    // Entity를 DTO로 변환하는 정적 메서드
    public static MessageResponseDto from(MessageEntity entity) {
        return MessageResponseDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .senderNickname(entity.getSender().getNickname())
                .receiverNickname(entity.getReceiver().getNickname())
                .isRead(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .build();
    }
}