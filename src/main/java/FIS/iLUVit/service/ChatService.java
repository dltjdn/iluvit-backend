package FIS.iLUVit.service;

import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.enumtype.NotificationTitle;
import FIS.iLUVit.dto.chat.ChatDetailResponse;
import FIS.iLUVit.dto.chat.ChatRoomResponse;
import FIS.iLUVit.dto.chat.ChatRoomCreateRequest;
import FIS.iLUVit.dto.chat.ChatCreateRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.ChatAlarm;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AlarmRepository alarmRepository;
    private final BlockedRepository blockedRepository;

    /**
     * 쪽지 작성 ( 대화방 생성 )
     */
    public void saveNewChat(Long userId, ChatRoomCreateRequest request) {

        User sendUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        User receiveUser;

        Long postId = request.getPostId();
        Long comment_id = request.getCommentId();

        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        Boolean anonymousInfo;

        if (request.getCommentId() != null) {
            Comment findComment = commentRepository.findById(comment_id)
                    .orElseThrow(() -> new CommentException(CommentErrorResult.COMMENT_NOT_FOUND));
            anonymousInfo = findComment.getAnonymous();
            receiveUser = findComment.getUser();
            if (receiveUser == null) {
                throw new CommentException(CommentErrorResult.COMMENT_NOT_FOUND);
            }
        } else {
            anonymousInfo = findPost.getAnonymous();
            receiveUser = findPost.getUser();
        }

        if(receiveUser.getNickName() == "알 수 없음"){
            throw new ChatException(ChatErrorResult.WITHDRAWN_USER);
        }

        if (Objects.equals(userId, receiveUser.getId())) {
            throw new ChatException(ChatErrorResult.CANNOT_SEND_TO_SELF);
        }

        Chat myChat = new Chat(request.getMessage(), receiveUser, sendUser);
        Chat partnerChat = new Chat(request.getMessage(), receiveUser, sendUser);

        ChatRoom myRoom = validateChatRoom(sendUser, receiveUser, comment_id, findPost, myChat, anonymousInfo);
        ChatRoom partnerRoom = validateChatRoom(receiveUser, sendUser, comment_id, findPost, partnerChat, anonymousInfo);
        myRoom.updatePartnerId(partnerRoom.getId());
        partnerRoom.updatePartnerId(myRoom.getId());

        // 쪽지를 받은 유저가 자신이 차단한 유저를 조회
        List<User> blockedUsers = blockedRepository.findByBlockingUser(receiveUser).stream()
                .map(Blocked::getBlockedUser)
                .collect(Collectors.toList());

        // 쪽지를 보낸 유저가 쪽지를 받는 유저에게 차단된 상태라면 알림을 발행하지 않음
        if(!blockedUsers.contains(sendUser)) {
            Alarm alarm = new ChatAlarm(receiveUser, sendUser, anonymousInfo);
            alarmRepository.save(alarm);
            AlarmUtils.publishAlarmEvent(alarm, NotificationTitle.ILUVIT.getDescription());
        }

        chatRepository.save(myChat);
        chatRepository.save(partnerChat);
    }

    /**
     * 쪽지 작성 ( 대화방 생성 후 쪽지 작성 )
     */
    public void saveChatInRoom(Long userId, ChatCreateRequest request) {

        ChatRoom myRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ChatException(ChatErrorResult.CHAT_ROOM_NOT_FOUND));

        if (myRoom.getReceiver() == null || myRoom.getSender() == null || myRoom.getSender().getNickName() == "알 수 없음") {
            throw new ChatException(ChatErrorResult.WITHDRAWN_USER);
        }

        if (!Objects.equals(myRoom.getReceiver().getId(), userId)) {
            throw new ChatException(ChatErrorResult.FORBIDDEN_ACCESS);
        }

        Long partnerUserId = myRoom.getSender().getId();


        if (Objects.equals(userId, partnerUserId)) {
            throw new ChatException(ChatErrorResult.CANNOT_SEND_TO_SELF);
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
                    .orElseThrow(() -> new ChatException(ChatErrorResult.CHAT_ROOM_NOT_FOUND));
        }
        myRoom.updatePartnerId(partnerRoom.getId());
        partnerRoom.updatePartnerId(myRoom.getId());
        partnerChat.updateChatRoom(partnerRoom);

        userRepository.findById(partnerUserId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND))
                .updateReadAlarm(Boolean.FALSE);

        chatRepository.save(myChat);
        chatRepository.save(partnerChat);
    }

    /**
     * 대화방 전체 조회
     */
    public Slice<ChatRoomResponse> findChatRoomList(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        Slice<ChatRoom> chatList = chatRoomRepository.findByReceiverOrderByUpdatedDateDesc(user, pageable);
        return chatList.map(chat -> {
            ChatRoomResponse chatRoomResponse = new ChatRoomResponse(chat);
            String profileImagePath = chat.getSender().getProfileImagePath();
            if (profileImagePath != null) chatRoomResponse.updateImage(profileImagePath);
            return chatRoomResponse;
        });
    }

    /**
     * 대화방 상세 조회
     */
    public ChatDetailResponse findChatRoomDetails(Long userId, Long roomId, Pageable pageable) {

        User receiverUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        ChatRoom findRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorResult.CHAT_ROOM_NOT_FOUND));

        User senderUser = findRoom.getSender();

        // 쪽지를 받은 유저가 자신이 차단한 유저를 조회
        List<User> blockedUsers = blockedRepository.findByBlockingUser(receiverUser)
                .stream()
                .map(Blocked::getBlockedUser)
                .collect(Collectors.toList());

        Slice<Chat> chatList;
        // 채팅 상대방이 사용자에게 차단된 상태인지 여부
        boolean opponentIsBlocked;

        // 차단 관계 유무에 따른 채팅 리스트 조회
        if (blockedUsers.contains(senderUser)) {
            // 쪽지를 보낸 유저가 차단된 유저인 경우
            Blocked blocked = blockedRepository.findByBlockingUserAndBlockedUser(receiverUser, senderUser)
                    .orElseThrow(() -> new BlockedException(BlockedErrorResult.NOT_EXIST_BLOCKED));
            LocalDateTime blockedDate = blocked.getCreatedDate();
            opponentIsBlocked = true;
            // 차단된 이후의 채팅은 조회하지 않음
            chatList = chatRepository.findByChatRoom(userId, roomId, blockedDate, pageable);
        } else {
            // 쪽지를 보낸 유저와 차단관계가 없는 경우
            chatList = chatRepository.findByChatRoom(userId, roomId, pageable);
            opponentIsBlocked = false;
        }

        Slice<ChatDetailResponse.ChatInfo> chatInfos = chatList.map(ChatDetailResponse.ChatInfo::new);
        ChatDetailResponse chatDto = new ChatDetailResponse(findRoom, chatInfos, opponentIsBlocked);
        String profileImagePath = senderUser.getProfileImagePath();
        if(profileImagePath != null) chatDto.updateImage(profileImagePath);

        return chatDto;
    }

    /**
     * 대화방 삭제
     */
    public void deleteChatRoom(Long userId, Long roomId) {
        chatRoomRepository.findById(roomId)
                .ifPresent(cr -> {
                    if (cr.getReceiver() == null) {
                        throw new ChatException(ChatErrorResult.WITHDRAWN_USER);
                    }
                    if (!Objects.equals(cr.getReceiver().getId(), userId)) {
                        throw new ChatException(ChatErrorResult.FORBIDDEN_ACCESS);
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
