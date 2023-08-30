
package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.dto.chat.ChatDto;
import FIS.iLUVit.dto.chat.ChatListDto;
import FIS.iLUVit.dto.chat.ChatRequest;
import FIS.iLUVit.dto.chat.ChatRoomRequest;
import FIS.iLUVit.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "쪽지 API")
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
    @Operation(summary = "쪽지 생성 (대화방 생성)", description = "쪽지 작성을 하여 대화방을 생성합니다.")
    @PostMapping("")
    public Long createChat(@Login Long userId, @RequestBody ChatRequest request) {
        return chatService.saveNewChat(userId, request);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 쪽지 작성 ( 대화방 생성 후 쪽지 작성 )
     */
    @Operation(summary = "쪽지 생성 (대화방 생성 후)", description = "대화방 생성 후 쪽지를 작성합니다.")
    @PostMapping("in-room")
    public Long createChatInRoom(@Login Long userId, @RequestBody ChatRoomRequest request) {
        return chatService.saveChatInRoom(userId, request);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 대화방 전체 조회
     */
    @Operation(summary = "대화방 전체 조회", description = "나의 쪽지함에서 대화방 목록을 조회합니다.")
    @GetMapping("")
    public Slice<ChatListDto> getAllChatRoom(@Login Long userId, Pageable pageable) {
        return chatService.findChatRoomList(userId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 대화방 상세 조회
     */
    @Operation(summary = "대화방 상세 조회", description = "대화방의 쪽지 목록을 조회합니다.")
    @GetMapping("{roomId}")
    public ChatDto getChatRoomDetails(@Login Long userId, @PathVariable("roomId") Long roomId,
                                Pageable pageable) {
        return chatService.findChatRoomDetails(userId, roomId, pageable);
    }

    /**
     * 작성자: 이창윤
     * 작성내용: 대화방 삭제
     */
    @Operation(summary = "대화방 삭제", description = "대화방의 모든 쪽지와 대화방을 삭제합니다.")
    @DeleteMapping("{roomId}")
    public Long deleteChatRoom(@Login Long userId, @PathVariable("roomId") Long roomId) {
        return chatService.deleteChatRoom(userId, roomId);
    }

}