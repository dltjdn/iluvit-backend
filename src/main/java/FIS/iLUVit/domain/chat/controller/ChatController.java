
package FIS.iLUVit.domain.chat.controller;

import FIS.iLUVit.global.config.argumentResolver.Login;
import FIS.iLUVit.domain.chat.dto.ChatDetailResponse;
import FIS.iLUVit.domain.chat.dto.ChatRoomFindAllResponse;
import FIS.iLUVit.domain.chat.dto.ChatRoomCreateRequest;
import FIS.iLUVit.domain.chat.dto.ChatCreateRequest;
import FIS.iLUVit.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("chat")
public class ChatController {
    private final ChatService chatService;

    /**
     * COMMON
     */

    /**
     * 쪽지 작성 ( 대화방 생성 )
     */
    @PostMapping("")
    public ResponseEntity<Long> createChat(@Login Long userId, @RequestBody ChatRoomCreateRequest chatRoomCreateRequest) {
        Long response = chatService.saveNewChat(userId, chatRoomCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 쪽지 작성 ( 대화방 생성 후 쪽지 작성 )
     */
    @PostMapping("in-room")
    public ResponseEntity<Long> createChatInRoom(@Login Long userId, @RequestBody ChatCreateRequest chatCreateRequest) {
        Long response = chatService.saveChatInRoom(userId, chatCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 대화방 전체 조회
     */
    @GetMapping("")
    public ResponseEntity<Slice<ChatRoomFindAllResponse>> getAllChatRoom(@Login Long userId, Pageable pageable) {
        Slice<ChatRoomFindAllResponse> responses = chatService.findChatRoomList(userId, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 대화방 상세 조회
     */
    @GetMapping("{roomId}")
    public ResponseEntity<ChatDetailResponse> getChatRoomDetails(@Login Long userId, @PathVariable("roomId") Long roomId, Pageable pageable) {
        ChatDetailResponse responses = chatService.findChatRoomDetails(userId, roomId, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * 대화방 삭제
     */
    @DeleteMapping("{roomId}")
    public ResponseEntity<Long> deleteChatRoom(@Login Long userId, @PathVariable("roomId") Long roomId) {
        Long response = chatService.deleteChatRoom(userId, roomId);
        return ResponseEntity.ok(response);
    }

}