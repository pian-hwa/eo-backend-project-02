package com.example.imprint.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private String email;
    private String nickname;
    private String name;

    // 반환되는 값들
    public static UserResponseDto fromEntity(UserEntity user) {
        return UserResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .name(user.getName())
                .build();
    }
}