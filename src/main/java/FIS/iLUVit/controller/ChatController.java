
package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.chat.ChatDto;
import FIS.iLUVit.dto.chat.ChatListDto;
import FIS.iLUVit.dto.chat.ChatRequest;
import FIS.iLUVit.dto.chat.ChatRoomRequest;
import FIS.iLUVit.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
     * 작성자: 이창윤
     * 작성내용: 쪽지 작성 ( 대화방 생성 )
     */
    @PostMapping("")
    public Long createChat(@Login Long userId, @RequestBody ChatRequest request) {
        return chatService.saveNewChat(userId, request);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 쪽지 작성 ( 대화방 생성 후 쪽지 작성 )
     */
    @PostMapping("in-room")
    public Long createChatInRoom(@Login Long userId, @RequestBody ChatRoomRequest request) {
        return chatService.saveChatInRoom(userId, request);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 대화방 전체 조회
     */
    @GetMapping("")
    public Slice<ChatListDto> getAllChatRoom(@Login Long userId, Pageable pageable) {
        return chatService.findChatRoomList(userId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 대화방 상세 조회
     */
    @GetMapping("{roomId}")
    public ChatDto getChatRoomDetails(@Login Long userId, @PathVariable("roomId") Long roomId,
                                Pageable pageable) {
        return chatService.findChatRoomDetails(userId, roomId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 대화방 삭제
     */
    @DeleteMapping("{roomId}")
    public Long deleteChatRoom(@Login Long userId, @PathVariable("roomId") Long roomId) {
        return chatService.deleteChatRoom(userId, roomId);
    }

}