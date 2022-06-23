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

    @PostMapping("/chat")
    public void createChat(@Login Long userId, @RequestBody CreateChatRequest request) {
        chatService.saveChat(userId, request);
    }

    @GetMapping("/chat/list")
    public Slice<ChatListDTO> findAll(@Login Long userId, Pageable pageable) {
        return chatService.findAll(userId, pageable);
    }

    @GetMapping("/chat/{post_id}")
    public Slice<ChatDTO> searchByPost(@Login Long userId, @PathVariable("post_id") Long postId,
                                           Pageable pageable) {
        return chatService.findByPost(userId, postId, pageable);
    }
}
