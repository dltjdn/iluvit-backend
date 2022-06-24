package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ChatDTO;
import FIS.iLUVit.controller.dto.ChatListDTO;
import FIS.iLUVit.controller.dto.CreateChatRequest;
import FIS.iLUVit.domain.Chat;
import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.CommentException;
import FIS.iLUVit.exception.PostException;
import FIS.iLUVit.exception.UserException;
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

    public void saveChat(Long userId, CreateChatRequest request) {
        User sendUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저"));
        User receiveUser = userRepository.findById(request.getReceiver_id())
                .orElseThrow(() -> new UserException("존재하지 않는 유저"));

        Long post_id = request.getPost_id();
        Long comment_id = request.getComment_id();

        Post findPost = postRepository.findById(post_id)
                .orElseThrow(() -> new PostException("존재하지 않는 게시글"));
        Chat chat = new Chat(request.getMessage(), receiveUser,
                sendUser, findPost);
        if (comment_id != null) {
            Comment findComment = commentRepository.findById(comment_id)
                    .orElseThrow(() -> new CommentException("존재하지 않는 댓글"));
            chat.updateComment(findComment);
        }

        chatRepository.save(chat);

    }

    public Slice<ChatListDTO> findAll(Long userId, Pageable pageable) {
        Slice<Chat> chatList = chatRepository.findFirstByPost(userId, pageable);
        List<ChatListDTO> content = chatList.getContent().stream()
                .map(c -> new ChatListDTO(c))
                .collect(Collectors.toList());

        boolean hasNext = false;
        if(content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    public Slice<ChatDTO> findByPost(Long userId, Long postId, Pageable pageable) {
        Slice<Chat> chatList = chatRepository.findByPost(userId, postId, pageable);
        List<ChatDTO> content = chatList.getContent().stream()
                .map(c -> new ChatDTO(c))
                .collect(Collectors.toList());

        boolean hasNext = false;
        if(content.size() > pageable.getPageSize()){
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
