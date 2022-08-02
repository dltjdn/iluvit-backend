
package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.ChatDTO;
import FIS.iLUVit.controller.dto.ChatListDTO;
import FIS.iLUVit.controller.dto.CreateChatRequest;
import FIS.iLUVit.controller.dto.CreateChatRoomRequest;
import FIS.iLUVit.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     작성자: 이창윤
     작성시간: 2022/06/24 3:11 PM
     내용: 쪽지 작성 ( 대화방 생성 )
     */
    @PostMapping("/user/chat")
    public Long createChat(@Login Long userId, @RequestBody CreateChatRequest request) {
        return chatService.saveChat(userId, request);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/24 3:11 PM
     내용: 쪽지 작성 ( 대화방 생성 후 쪽지 작성 )
     */
    @PostMapping("/user/chat/inRoom")
    public Long createChatInRoom(@Login Long userId, @RequestBody CreateChatRoomRequest request) {
        return chatService.saveChatInRoom(userId, request);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/24 3:10 PM
     내용: 나의 쪽지함 (대화 상대 목록) 조회, 최신순 정렬로 대화 상대와 함께 쪽지 목록을 보여줌.
     */
    @GetMapping("/user/chat/list")
    public Slice<ChatListDTO> findAll(@Login Long userId, Pageable pageable) {
        return chatService.findAll(userId, pageable);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/24 4:34 PM
        내용: 쪽지 자세히 보기
    */
    @GetMapping("/user/chat/{room_id}")
    public ChatDTO searchByPost(@Login Long userId, @PathVariable("room_id") Long roomId,
                                           Pageable pageable) {
        return chatService.findByOpponent(userId, roomId, pageable);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/29 4:13 PM
        내용: 대화방 모든 쪽지 삭제 ( 대화방 삭제 )
    */
    @DeleteMapping("/user/chat/{room_id}")
    public Long deleteChatRoom(@Login Long userId, @PathVariable("room_id") Long roomId) {
        return chatService.deleteChatRoom(userId, roomId);
    }
}
