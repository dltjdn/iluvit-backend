package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.ChatDTO;
import FIS.iLUVit.controller.dto.ChatListDTO;
import FIS.iLUVit.controller.dto.CreateChatRequest;
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
     내용: 쪽지 작성
     */
    @PostMapping("/chat")
    public void createChat(@Login Long userId, @RequestBody CreateChatRequest request) {
        chatService.saveChat(userId, request);
    }

    /**
     작성자: 이창윤
     작성시간: 2022/06/24 3:10 PM
     내용: 나의 쪽지함 (대화 상대 목록) 조회, 최신순 정렬
     */
    @GetMapping("/chat/list")
    public Slice<ChatListDTO> findAll(@Login Long userId, Pageable pageable) {
        return chatService.findAll(userId, pageable);
    }

    /**
        작성자: 이창윤
        작성시간: 2022/06/24 4:34 PM
        내용: 쪽지 자세히 보기
    */
    @GetMapping("/chat/{other_id}")
    public Slice<ChatDTO> searchByPost(@Login Long userId, @PathVariable("other_id") Long otherId,
                                           Pageable pageable) {
        return chatService.findByOpponent(userId, otherId, pageable);
    }
}
