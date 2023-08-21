package FIS.iLUVit.service;

import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.dto.chat.ChatListDto;
import FIS.iLUVit.dto.chat.ChatRoomDto;
import FIS.iLUVit.dto.chat.ChatRoomRequestDto;
import FIS.iLUVit.dto.chat.ChatRequestDto;
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

    private final AlarmRepository alarmRepository;

    /**
     * 쪽지 작성 ( 대화방 생성 )
     */
    public void saveNewChat(Long userId, ChatRoomRequestDto request) {

        if (userId == null) {
            throw new ChatException(ChatErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        User sendUser = userRepository.findById(userId)
                .orElseThrow(() -> new ChatException(ChatErrorResult.USER_NOT_EXIST));

        User receiveUser;

        Long post_id = request.getPostId();
        Long comment_id = request.getCommentId();

        Post findPost = postRepository.findById(post_id)
                .orElseThrow(() -> new ChatException(ChatErrorResult.POST_NOT_EXIST));

        Boolean anonymousInfo;

        if (request.getCommentId() != null) {
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
        String type = "아이러빗";
        AlarmUtils.publishAlarmEvent(alarm, type);

        chatRepository.save(myChat);
        chatRepository.save(partnerChat);
    }

    /**
     * 쪽지 작성 ( 대화방 생성 후 쪽지 작성 )
     */
    public void saveChatInRoom(Long userId, ChatRequestDto request) {

        if (userId == null) {
            throw new ChatException(ChatErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        ChatRoom myRoom = chatRoomRepository.findById(request.getRoomId())
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

        chatRepository.save(myChat);
        chatRepository.save(partnerChat);
    }

    /**
     * 대화방 전체 조회
     */
    public Slice<ChatRoomDto> findChatRoomList(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChatException(ChatErrorResult.USER_NOT_EXIST));

        Slice<ChatRoom> chatList = chatRoomRepository.findByReceiverOrderByUpdatedDateDesc(user, pageable);
        return chatList.map(chat -> {
            ChatRoomDto chatRoomDto = new ChatRoomDto(chat);
            String profileImagePath = chat.getSender().getProfileImagePath();
            if (profileImagePath != null) chatRoomDto.updateImage(profileImagePath);
            return chatRoomDto;
        });
    }

    /**
     * 대화방 상세 조회
     */
    public ChatListDto findChatRoomDetails(Long userId, Long roomId, Pageable pageable) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChatException(ChatErrorResult.USER_NOT_EXIST));

        ChatRoom findRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorResult.ROOM_NOT_EXIST));

        Slice<Chat> chatList = chatRepository.findByChatRoomAndChatRoomReceiverOrderByCreatedDateDesc(findRoom, user, pageable);

        Slice<ChatListDto.ChatInfo> chatInfos = chatList.map(ChatListDto.ChatInfo::new);
        ChatListDto chatListDto = new ChatListDto(findRoom, chatInfos);
        String profileImagePath = findRoom.getSender().getProfileImagePath();
        if(profileImagePath != null) chatListDto.updateImage(profileImagePath);
        return chatListDto;
    }

    /**
     * 대화방 삭제
     */
    public void deleteChatRoom(Long userId, Long roomId) {
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
    }

    /**
     * 대화방이 있는지 검증해준 후 없으면 새로 생성해준다
     */
    private ChatRoom validateChatRoom(User sendUser, User receiveUser, Long comment_id, Post post, Chat chat, Boolean anonymous) {
        ChatRoom chatRoom = chatRoomRepository.findByReceiverAndSenderAndPostAndAnonymous(receiveUser, sendUser, post, anonymous)
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
}
