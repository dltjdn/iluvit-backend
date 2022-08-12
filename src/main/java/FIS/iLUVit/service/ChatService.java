package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ChatDTO;
import FIS.iLUVit.controller.dto.ChatListDTO;
import FIS.iLUVit.controller.dto.CreateChatRequest;
import FIS.iLUVit.controller.dto.CreateChatRoomRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.ChatAlarm;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ImageService imageService;

    public Long saveChat(Long userId, CreateChatRequest request) {

        if (userId == null) {
            throw new ChatException(ChatErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        User sendUser = userRepository.findById(userId)
                .orElseThrow(() -> new ChatException(ChatErrorResult.USER_NOT_EXIST));

        User receiveUser;

        Long post_id = request.getPost_id();
        Long comment_id = request.getComment_id();

        Post findPost = postRepository.findById(post_id)
                .orElseThrow(() -> new ChatException(ChatErrorResult.POST_NOT_EXIST));

        Boolean anonymousInfo;

        if (request.getComment_id() != null) {
            Comment findComment = commentRepository.findById(comment_id)
                    .orElseThrow(() -> new CommentException(CommentErrorResult.NO_EXIST_COMMENT));
            anonymousInfo = findComment.getAnonymous();
            receiveUser = findComment.getUser();
        } else {
            anonymousInfo = findPost.getAnonymous();
            receiveUser = findPost.getUser();
        }

        if (Objects.equals(userId, receiveUser.getId())) {
            throw new ChatException(ChatErrorResult.NO_SEND_TO_SELF);
        }

        Chat chat1 = new Chat(request.getMessage(), receiveUser, sendUser);
        Chat chat2 = new Chat(request.getMessage(), receiveUser, sendUser);

        ChatRoom chatRoom1 = validateChatRoom(sendUser, receiveUser, comment_id, findPost, chat1, anonymousInfo);
        ChatRoom chatRoom2 = validateChatRoom(receiveUser, sendUser, comment_id, findPost, chat2, anonymousInfo);
        chatRoom1.updatePartnerId(chatRoom2.getId());
        chatRoom2.updatePartnerId(chatRoom1.getId());

        AlarmUtils.publishAlarmEvent(new ChatAlarm(receiveUser, sendUser, anonymousInfo));


        chatRepository.save(chat1);
        Chat savedChat = chatRepository.save(chat2);

        return savedChat.getId();

    }

    private ChatRoom validateChatRoom(User sendUser, User receiveUser, Long comment_id, Post post, Chat chat, Boolean anonymous) {
        ChatRoom findRoom = chatRoomRepository.findByReceiverAndSenderAndPostAndAnonymous(
                receiveUser, sendUser, post, anonymous)
                .orElse(null);
        if (findRoom == null) {
            // 대화방 없으면 새로 생성
            ChatRoom chatRoom = new ChatRoom(receiveUser, sendUser, post, anonymous);
            // 댓글 작성자와 쪽지 교환이면 comment 정보도 엮여줌.
            if (comment_id != null) {
                Comment findComment = commentRepository.getById(comment_id);
                chatRoom.updateComment(findComment);
            }
            chatRoomRepository.save(chatRoom);
            chat.updateChatRoom(chatRoom);
            return chatRoom;
        } else {
            chat.updateChatRoom(findRoom);
            return findRoom;
        }
    }

    public Slice<ChatListDTO> findAll(Long userId, Pageable pageable) {
        Slice<ChatRoom> chatList = chatRoomRepository.findByUser(userId, pageable);
        return chatList.map(c -> {
            ChatListDTO chatListDTO = new ChatListDTO(c);
            String profileImage = imageService.getProfileImage(c.getSender());
            chatListDTO.updateImage(profileImage);
            return chatListDTO;
        });
    }

    public ChatDTO findByOpponent(Long userId, Long roomId, Pageable pageable) {
        ChatRoom findRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorResult.ROOM_NOT_EXIST));

        Slice<Chat> chatList = chatRepository.findByChatRoom(userId, roomId, pageable);

        Slice<ChatDTO.ChatInfo> chatInfos = chatList.map(ChatDTO.ChatInfo::new);
        ChatDTO chatDTO = new ChatDTO(findRoom, chatInfos);
        String profileImage = imageService.getProfileImage(findRoom.getSender());
        chatDTO.updateImage(profileImage);
        return chatDTO;
    }

    public Long deleteChatRoom(Long userId, Long roomId) {
        chatRoomRepository.findById(roomId)
                .ifPresent(cr -> {
                    if (cr.getReceiver() == null) {
                        throw new ChatException(ChatErrorResult.WITHDRAWN_MEMBER);
                    }
                    if (!Objects.equals(cr.getReceiver().getId(), userId)) {
                        throw new ChatException(ChatErrorResult.UNAUTHORIZED_USER_ACCESS);
                    }
                    if (cr.getPartner_id() != null) {
                        chatRoomRepository.findById(cr.getPartner_id())
                                .ifPresent(c -> c.updatePartnerId(null));
                    }
                });
        chatRoomRepository.deleteById(roomId);
        return roomId;
    }

    public Long saveChatInRoom(Long userId, CreateChatRoomRequest request) {

        if (userId == null) {
            throw new ChatException(ChatErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        ChatRoom findRoom = chatRoomRepository.findById(request.getRoom_id())
                .orElseThrow(() -> new ChatException(ChatErrorResult.ROOM_NOT_EXIST));

        if (findRoom.getReceiver() == null || findRoom.getSender() == null) {
            throw new ChatException(ChatErrorResult.WITHDRAWN_MEMBER);
        }

        if (!Objects.equals(findRoom.getReceiver().getId(), userId)) {
            throw new ChatException(ChatErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        Long partnerUserId = findRoom.getSender().getId();

        if (Objects.equals(userId, partnerUserId)) {
            throw new ChatException(ChatErrorResult.NO_SEND_TO_SELF);
        }

        User sendUser = userRepository.getById(userId);
        User receiveUser = userRepository.getById(partnerUserId);
        Chat chat1 = new Chat(request.getMessage(), receiveUser, sendUser);
        Chat chat2 = new Chat(request.getMessage(), receiveUser, sendUser);

        chat1.updateChatRoom(findRoom);

        // 삭제된 대화방이면 새로 생성
        ChatRoom chatRoom;
        if (findRoom.getPartner_id() == null) {
            chatRoom = new ChatRoom(receiveUser, sendUser, findRoom.getPost(), findRoom.getAnonymous());
            chatRoomRepository.save(chatRoom);
        } else {
            chatRoom = chatRoomRepository.findById(findRoom.getPartner_id())
                    .orElseThrow(() -> new ChatException(ChatErrorResult.ROOM_NOT_EXIST));
        }
        findRoom.updatePartnerId(chatRoom.getId());
        chatRoom.updatePartnerId(findRoom.getId());
        chat2.updateChatRoom(chatRoom);

        Chat savedChat = chatRepository.save(chat1);
        chatRepository.save(chat2);

//        AlarmUtils.publishAlarmEvent(new ChatAlarm(receiveUser, sendUser, findRoom.getAnonymous()));

        return savedChat.getId();
    }

}
