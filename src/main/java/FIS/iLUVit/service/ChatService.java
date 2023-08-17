package FIS.iLUVit.service;

import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.dto.chat.ChatDto;
import FIS.iLUVit.dto.chat.ChatListDto;
import FIS.iLUVit.dto.chat.ChatRequest;
import FIS.iLUVit.dto.chat.ChatRoomRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.ChatAlarm;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final BlockedRepository blockedRepository;
    private final AlarmRepository alarmRepository;
    private final ImageService imageService;

    public Long saveNewChat(Long userId, ChatRequest request) {
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

        if(receiveUser.getNickName().equals("알 수 없음")){
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

        // 쪽지를 받은 유저가 자신이 차단한 유저를 조회
        List<User> blockedUsers = getBlockedUsers(receiveUser);

        // 쪽지를 보낸 유저가 쪽지를 받는 유저에게 차단된 상태라면 알림을 발행하지 않음
        if(!blockedUsers.contains(sendUser)) {
            Alarm alarm = new ChatAlarm(receiveUser, sendUser, anonymousInfo);
            alarmRepository.save(alarm);
            String type = "아이러빗";
            AlarmUtils.publishAlarmEvent(alarm, type);
        }

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

    public Slice<ChatListDto> findChatRoomList(Long userId, Pageable pageable) {
        Slice<ChatRoom> chatList = chatRoomRepository.findByUser(userId, pageable);
        return chatList.map(c -> {
            ChatListDto chatListDto = new ChatListDto(c);
            String profileImage = imageService.getProfileImage(c.getSender());
            chatListDto.updateImage(profileImage);
            return chatListDto;
        });
    }

    public ChatDto findChatRoomDetails(Long userId, Long roomId, Pageable pageable) {
        ChatRoom findRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorResult.ROOM_NOT_EXIST));
        User receiverUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        User senderUser = findRoom.getSender();

        // 쪽지를 받은 유저가 자신이 차단한 유저를 조회
        List<User> blockedUsers = getBlockedUsers(receiverUser);

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

        Slice<ChatDto.ChatInfo> chatInfos = chatList.map(ChatDto.ChatInfo::new);
        ChatDto chatDto = new ChatDto(findRoom, chatInfos, opponentIsBlocked);
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

        if (myRoom.getReceiver() == null || myRoom.getSender() == null || myRoom.getSender().getNickName().equals("알 수 없음")) {
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

        myChat.updateChatRoom(myRoom);

        // 쪽지를 받은 유저가 자신이 차단한 유저를 조회
        List<User> blockedUsers = getBlockedUsers(receiveUser);

        // 쪽지를 보낸 유저가 쪽지를 받는 유저에게 차단된 상태가 아니라면 실행
        if (!blockedUsers.contains(sendUser)) {
            // 삭제된 대화방이면 새로 생성
            ChatRoom partnerRoom;
            if (myRoom.getPartner_id() == null) {
                partnerRoom = new ChatRoom(receiveUser, sendUser, myRoom.getPost(), myRoom.getAnonymous());
                chatRoomRepository.save(partnerRoom);
            } else {
                partnerRoom = chatRoomRepository.findById(myRoom.getPartner_id())
                        .orElseThrow(() -> new ChatException(ChatErrorResult.ROOM_NOT_EXIST));
            }

            partnerRoom.updatePartnerId(myRoom.getId());

            Chat partnerChat = new Chat(request.getMessage(), receiveUser, sendUser);
            partnerChat.updateChatRoom(partnerRoom);
            chatRepository.save(partnerChat);
        }

        userRepository.findById(partnerUserId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .updateReadAlarm(Boolean.FALSE);

        Chat savedChat = chatRepository.save(myChat);

        return savedChat.getId();
    }

    /**
     * 해당 유저가 차단한 유저 리스트를 조회하여 반환합니다
     */
    private List<User> getBlockedUsers(User user) {
        return blockedRepository.findByBlockingUser(user)
                .stream()
                .map(Blocked::getBlockedUser)
                .collect(Collectors.toList());
    }

}
