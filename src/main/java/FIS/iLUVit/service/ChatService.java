package FIS.iLUVit.service;

import FIS.iLUVit.domain.iluvit.Alarm;
import FIS.iLUVit.domain.iluvit.*;
import FIS.iLUVit.dto.chat.ChatDto;
import FIS.iLUVit.dto.chat.ChatListDto;
import FIS.iLUVit.dto.chat.ChatRequest;
import FIS.iLUVit.dto.chat.ChatRoomRequest;
import FIS.iLUVit.domain.iluvit.ChatAlarm;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.iluvit.*;
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

    private final AlarmRepository alarmRepository;

    public Long saveChat(Long userId, ChatRequest request) {

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
            if (receiveUser == null) {
                throw new CommentException(CommentErrorResult.NO_EXIST_COMMENT);
            }
        } else {
            anonymousInfo = findPost.getAnonymous();
            receiveUser = findPost.getUser();
        }

        if(receiveUser.getNickName() == "알 수 없음"){
            throw new ChatException(ChatErrorResult.WITHDRAWN_MEMBER);
        }

        if (Objects.equals(userId, receiveUser.getId())) {
            throw new ChatException(ChatErrorResult.NO_SEND_TO_SELF);
        }

        Chat myChat = new Chat(request.getMessage(), receiveUser, sendUser);
        Chat partnerChat = new Chat(request.getMessage(), receiveUser, sendUser);

        ChatRoom myRoom = validateChatRoom(sendUser, receiveUser, comment_id, findPost, myChat, anonymousInfo);
        ChatRoom partnerRoom = validateChatRoom(receiveUser, sendUser, comment_id, findPost, partnerChat, anonymousInfo);
        myRoom.updatePartnerId(partnerRoom.getId());
        partnerRoom.updatePartnerId(myRoom.getId());

        Alarm alarm = new ChatAlarm(receiveUser, sendUser, anonymousInfo);
        alarmRepository.save(alarm);
        AlarmUtils.publishAlarmEvent(alarm);

        chatRepository.save(myChat);
        Chat savedChat = chatRepository.save(partnerChat);

        return savedChat.getId();

    }

    private ChatRoom validateChatRoom(User sendUser, User receiveUser, Long comment_id, Post post, Chat chat, Boolean anonymous) {
        ChatRoom chatRoom = chatRoomRepository.findByReceiverAndSenderAndPostAndAnonymous(
                        receiveUser, sendUser, post, anonymous)
                .orElse(null);
        if (chatRoom == null) {
            // 대화방 없으면 새로 생성
            chatRoom = new ChatRoom(receiveUser, sendUser, post, anonymous);
            // 댓글 작성자와 쪽지 교환이면 comment 정보도 엮여줌.
            if (comment_id != null) {
                Comment findComment = commentRepository.getById(comment_id);
                chatRoom.updateComment(findComment);
            }
            chatRoomRepository.save(chatRoom);
        }
        chat.updateChatRoom(chatRoom);
        return chatRoom;
    }

    public Slice<ChatListDto> findAll(Long userId, Pageable pageable) {
        Slice<ChatRoom> chatList = chatRoomRepository.findByUser(userId, pageable);
        return chatList.map(c -> {
            ChatListDto chatListDto = new ChatListDto(c);
            String profileImage = imageService.getProfileImage(c.getSender());
            chatListDto.updateImage(profileImage);
            return chatListDto;
        });
    }

    public ChatDto findByOpponent(Long userId, Long roomId, Pageable pageable) {
        ChatRoom findRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorResult.ROOM_NOT_EXIST));

        Slice<Chat> chatList = chatRepository.findByChatRoom(userId, roomId, pageable);

        Slice<ChatDto.ChatInfo> chatInfos = chatList.map(ChatDto.ChatInfo::new);
        ChatDto chatDto = new ChatDto(findRoom, chatInfos);
        String profileImage = imageService.getProfileImage(findRoom.getSender());
        chatDto.updateImage(profileImage);
        return chatDto;
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

    public Long saveChatInRoom(Long userId, ChatRoomRequest request) {

        if (userId == null) {
            throw new ChatException(ChatErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        ChatRoom myRoom = chatRoomRepository.findById(request.getRoom_id())
                .orElseThrow(() -> new ChatException(ChatErrorResult.ROOM_NOT_EXIST));

        if (myRoom.getReceiver() == null || myRoom.getSender() == null || myRoom.getSender().getNickName() == "알 수 없음") {
            throw new ChatException(ChatErrorResult.WITHDRAWN_MEMBER);
        }

        if (!Objects.equals(myRoom.getReceiver().getId(), userId)) {
            throw new ChatException(ChatErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        Long partnerUserId = myRoom.getSender().getId();


        if (Objects.equals(userId, partnerUserId)) {
            throw new ChatException(ChatErrorResult.NO_SEND_TO_SELF);
        }

        User sendUser = userRepository.getById(userId);
        User receiveUser = userRepository.getById(partnerUserId);
        Chat myChat = new Chat(request.getMessage(), receiveUser, sendUser);
        Chat partnerChat = new Chat(request.getMessage(), receiveUser, sendUser);

        myChat.updateChatRoom(myRoom);

        // 삭제된 대화방이면 새로 생성
        ChatRoom partnerRoom;
        if (myRoom.getPartner_id() == null) {
            partnerRoom = new ChatRoom(receiveUser, sendUser, myRoom.getPost(), myRoom.getAnonymous());
            chatRoomRepository.save(partnerRoom);
        } else {
            partnerRoom = chatRoomRepository.findById(myRoom.getPartner_id())
                    .orElseThrow(() -> new ChatException(ChatErrorResult.ROOM_NOT_EXIST));
        }
        myRoom.updatePartnerId(partnerRoom.getId());
        partnerRoom.updatePartnerId(myRoom.getId());
        partnerChat.updateChatRoom(partnerRoom);

        userRepository.findById(partnerUserId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .updateReadAlarm(Boolean.FALSE);

        Chat savedChat = chatRepository.save(myChat);
        chatRepository.save(partnerChat);

        return savedChat.getId();
    }

}
