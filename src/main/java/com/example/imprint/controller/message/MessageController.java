package com.example.imprint.controller.message;

import com.example.imprint.domain.ApiResponseDto;
import com.example.imprint.domain.message.MessageRequestDto;
import com.example.imprint.domain.message.MessageResponseDto;
import com.example.imprint.security.user.CustomUserDetails;
import com.example.imprint.service.message.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 쪽지 보내기
    @PostMapping("/send")
    public ResponseEntity<ApiResponseDto<Void>> sendMessage(
            @RequestBody MessageRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        messageService.sendMessage(currentUser.getUsername(), requestDto.getReceiverNickname(), requestDto.getContent());
        return ResponseEntity.ok(ApiResponseDto.success(null, "쪽지가 성공적으로 전송되었습니다."));
    }

    // 받은 쪽지함 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<ApiResponseDto<List<MessageResponseDto>>> getMessageList(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<MessageResponseDto> messages = messageService.getReceivedMessages(currentUser.getUsername());
        return ResponseEntity.ok(ApiResponseDto.success(messages, "받은 쪽지 목록을 조회했습니다."));
    }

    // 보낸 쪽지함 리스트 조회 (추가 권장)
    @GetMapping("/sent")
    public ResponseEntity<ApiResponseDto<List<MessageResponseDto>>> getSentMessageList(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<MessageResponseDto> messages = messageService.getSentMessages(currentUser.getUsername());
        return ResponseEntity.ok(ApiResponseDto.success(messages, "보낸 쪽지 목록을 조회했습니다."));
    }

    // 쪽지 단건 읽기
    @GetMapping("/read/{id}")
    public ResponseEntity<ApiResponseDto<MessageResponseDto>> readMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        MessageResponseDto message = messageService.readMessage(id, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponseDto.success(message, "쪽지 상세 내용을 조회했습니다."));
    }

    // 보낸 쪽지 삭제
    @DeleteMapping("/sent/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteSentMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        messageService.deleteBySender(id, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponseDto.success(null, "보낸 쪽지가 삭제되었습니다."));
    }

    // 받은 쪽지 삭제
    @DeleteMapping("/received/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteReceivedMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        messageService.deleteByReceiver(id, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponseDto.success(null, "받은 쪽지가 삭제되었습니다."));
    }
}