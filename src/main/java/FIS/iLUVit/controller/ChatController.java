
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
     작성자: 이창윤
     작성시간: 2022/06/24 3:11 PM
     내용: 쪽지 작성 ( 대화방 생성 )
     */
    @PostMapping("")
    public Long createChat(@Login Long userId, @RequestBody ChatRequest request) {
        return chatService.saveChat(userId, request);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/24 3:11 PM
     내용: 쪽지 작성 ( 대화방 생성 후 쪽지 작성 )
     */
    @PostMapping("in-room")
    public Long createChatInRoom(@Login Long userId, @RequestBody ChatRoomRequest request) {
        return chatService.saveChatInRoom(userId, request);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/24 3:10 PM
     내용: 나의 쪽지함 (대화 상대 목록)
     */
    @GetMapping("")
    public Slice<ChatListDto> findAll(@Login Long userId, Pageable pageable) {
        return chatService.findAll(userId, pageable);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/24 4:34 PM
        내용: 쪽지 자세히 보기
    */
    @GetMapping("{roomId}")
    public ChatDto searchByPost(@Login Long userId, @PathVariable("roomId") Long roomId,
                                Pageable pageable) {
        return chatService.findByOpponent(userId, roomId, pageable);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/29 4:13 PM
        내용: 대화방 모든 쪽지 삭제 ( 대화방 삭제 )
    */
    @DeleteMapping("{roomId}")
    public Long deleteChatRoom(@Login Long userId, @PathVariable("roomId") Long roomId) {
        return chatService.deleteChatRoom(userId, roomId);
    }
}