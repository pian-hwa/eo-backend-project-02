package com.example.imprint.domain.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDto {
    private String receiverNickname; // 받는 사람 닉네임
    private String content;          // 쪽지 내용
}