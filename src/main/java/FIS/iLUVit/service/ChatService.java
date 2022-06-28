package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ChatDTO;
import FIS.iLUVit.controller.dto.ChatListDTO;
import FIS.iLUVit.controller.dto.CreateChatRequest;
import FIS.iLUVit.domain.Chat;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.alarms.ChatAlarm;
import FIS.iLUVit.exception.CommentException;
import FIS.iLUVit.repository.ChatRepository;
import FIS.iLUVit.repository.CommentRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Long saveChat(Long userId, CreateChatRequest request) {
        User sendUser = userRepository.getById(userId);
        User receiveUser = userRepository.getById(request.getReceiver_id());

        Long post_id = request.getPost_id();
        Long comment_id = request.getComment_id();

        Post findPost = postRepository.getById(post_id);

        Chat chat = new Chat(request.getMessage(), receiveUser,
                sendUser, findPost);

        // 댓글 작성자와 쪽지 교환이면 comment 정보도 엮여줌.
        if (comment_id != null) {
            Comment findComment = commentRepository.findById(comment_id)
                    .orElseThrow(() -> new CommentException("존재하지 않는 댓글"));
            chat.updateComment(findComment);
        }

        AlarmUtils.publishAlarmEvent(new ChatAlarm(receiveUser, sendUser));

        return chatRepository.save(chat).getId();

    }

    public Slice<ChatListDTO> findAll(Long userId, Pageable pageable) {
        Slice<Chat> chatList = chatRepository.findByUser(userId, pageable);
        return chatList.map(c -> new ChatListDTO(c));
    }

    public Slice<ChatDTO> findByOpponent(Long userId, Long otherId, Pageable pageable) {

        Slice<Chat> chatList = chatRepository.findByOpponent(userId, otherId, pageable);
        return chatList.map(c -> new ChatDTO(c));
    }
}
