
package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.chat.ChatListDto;
import FIS.iLUVit.dto.chat.ChatRoomDto;
import FIS.iLUVit.dto.chat.ChatRoomRequestDto;
import FIS.iLUVit.dto.chat.ChatRequestDto;
import FIS.iLUVit.service.ChatService;
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
    public ResponseEntity<Void> createChat(@Login Long userId, @RequestBody ChatRoomRequestDto chatRoomRequestDto) {
        chatService.saveNewChat(userId, chatRoomRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 쪽지 작성 ( 대화방 생성 후 쪽지 작성 )
     */
    @PostMapping("in-room")
    public ResponseEntity<Void> createChatInRoom(@Login Long userId, @RequestBody ChatRequestDto chatRequestDto) {
        chatService.saveChatInRoom(userId, chatRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 대화방 전체 조회
     */
    @GetMapping("")
    public ResponseEntity<Slice<ChatRoomDto>> getAllChatRoom(@Login Long userId, Pageable pageable) {
        Slice<ChatRoomDto> chatListDtos = chatService.findChatRoomList(userId, pageable);
        return ResponseEntity.ok(chatListDtos);
    }

    /**
     * 대화방 상세 조회
     */
    @GetMapping("{roomId}")
    public ResponseEntity<ChatListDto> getChatRoomDetails(@Login Long userId, @PathVariable("roomId") Long roomId, Pageable pageable) {
        ChatListDto chatListDtos = chatService.findChatRoomDetails(userId, roomId, pageable);
        return ResponseEntity.ok(chatListDtos);
    }

    /**
     * 대화방 삭제
     */
    @DeleteMapping("{roomId}")
    public ResponseEntity<Void> deleteChatRoom(@Login Long userId, @PathVariable("roomId") Long roomId) {
        chatService.deleteChatRoom(userId, roomId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}