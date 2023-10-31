package FIS.iLUVit.domain.chat.service;

import FIS.iLUVit.domain.alarm.service.AlarmService;
import FIS.iLUVit.domain.blocked.domain.Blocked;
import FIS.iLUVit.domain.blocked.exception.BlockedErrorResult;
import FIS.iLUVit.domain.blocked.exception.BlockedException;
import FIS.iLUVit.domain.blocked.repository.BlockedRepository;
import FIS.iLUVit.domain.chat.domain.Chat;
import FIS.iLUVit.domain.chat.domain.ChatRoom;
import FIS.iLUVit.domain.chat.exception.ChatErrorResult;
import FIS.iLUVit.domain.chat.exception.ChatException;
import FIS.iLUVit.domain.chat.repository.ChatRepository;
import FIS.iLUVit.domain.chat.repository.ChatRoomRepository;
import FIS.iLUVit.domain.comment.domain.Comment;
import FIS.iLUVit.domain.comment.exception.CommentErrorResult;
import FIS.iLUVit.domain.comment.exception.CommentException;
import FIS.iLUVit.domain.comment.repository.CommentRepository;
import FIS.iLUVit.domain.post.domain.Post;
import FIS.iLUVit.domain.post.exception.PostErrorResult;
import FIS.iLUVit.domain.post.exception.PostException;
import FIS.iLUVit.domain.post.repository.PostRepository;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.repository.UserRepository;
import FIS.iLUVit.domain.chat.dto.ChatDetailResponse;
import FIS.iLUVit.domain.chat.dto.ChatRoomFindAllResponse;
import FIS.iLUVit.domain.chat.dto.ChatRoomCreateRequest;
import FIS.iLUVit.domain.chat.dto.ChatCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final AlarmService alarmService;
    private final BlockedRepository blockedRepository;

    /**
     * 쪽지 작성 ( 대화방 생성 )
     */
    public Long saveNewChat(Long userId, ChatRoomCreateRequest request) {

        User user = getUser(userId);
        Long commentId = request.getCommentId();
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostException(PostErrorResult.POST_NOT_FOUND));

        Boolean anonymousInfo;
        User receiveUser;
        if (request.getCommentId() != null) {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentException(CommentErrorResult.COMMENT_NOT_FOUND));

            anonymousInfo = comment.getAnonymous();
            receiveUser = comment.getUser();

            if (receiveUser == null) {
                throw new CommentException(CommentErrorResult.COMMENT_NOT_FOUND);
            }
        } else {
            anonymousInfo = post.getAnonymous();
            receiveUser = post.getUser();
        }

        validateSaveChat(userId, receiveUser);

        Long chatId = createChatRoom(request, user, commentId, post, anonymousInfo, receiveUser);

        // 쪽지를 받은 유저가 자신이 차단한 유저를 조회
        List<User> blockedUsers = getBlockedUsers(receiveUser);

        // 쪽지를 보낸 유저가 쪽지를 받는 유저에게 차단된 상태라면 알림을 발행하지 않음
        if(!blockedUsers.contains(user)) {
            alarmService.sendChatAlarm(user, anonymousInfo, receiveUser);
        }
        return chatId;
    }

    /**
     * 쪽지 작성 ( 대화방 생성 후 쪽지 작성 )
     */
    public Long saveChatInRoom(Long userId, ChatCreateRequest request) {

        ChatRoom myRoom = getChatRoom(request.getRoomId());

        validateSaveChatAfterCreateChatRoom(userId, myRoom); // 채팅 생성 권한 체크

        Long partnerUserId = myRoom.getSender().getId();
        User sendUser = getUser(userId);
        User receiveUser = getUser(partnerUserId);

        // 삭제된 대화방이면 새로 생성
        ChatRoom partnerRoom;
        if (myRoom.getPartnerId() == null) {
            partnerRoom = ChatRoom.of(receiveUser, sendUser, myRoom.getPost(), myRoom.getAnonymous());
            chatRoomRepository.save(partnerRoom);
        } else {
            partnerRoom = chatRoomRepository.findById(myRoom.getPartnerId())
                    .orElseThrow(() -> new ChatException(ChatErrorResult.CHAT_ROOM_NOT_FOUND));
        }
        myRoom.updatePartnerId(partnerRoom.getId());
        partnerRoom.updatePartnerId(myRoom.getId());

        Long chatId = createChat(request.getMessage(), myRoom, receiveUser, sendUser, partnerRoom);

        getUser(partnerUserId)
                .updateReadAlarm(Boolean.FALSE);

        return chatId;
    }

    /**
     * 대화방 전체 조회
     */
    public Slice<ChatRoomFindAllResponse> findChatRoomList(Long userId, Pageable pageable) {
        User user = getUser(userId);
        Slice<ChatRoom> chatRooms = chatRoomRepository.findByReceiverOrderByUpdatedDateDesc(user, pageable);

        return chatRooms.map(ChatRoomFindAllResponse::from);
    }

    /**
     * 대화방 상세 조회
     */
    public ChatDetailResponse findChatRoomDetails(Long userId, Long roomId, Pageable pageable) {
        User receiverUser = getUser(userId);
        ChatRoom chatRoom = getChatRoom(roomId);
        User sender = chatRoom.getSender();

        // 쪽지를 받은 유저가 자신이 차단한 유저를 조회
        List<User> blockedUsers = getBlockedUsers(receiverUser);

        Slice<Chat> chatList;
        boolean opponentIsBlocked;// 채팅 상대방이 사용자에게 차단된 상태인지 여부

        // 차단 관계 유무에 따른 채팅 리스트 조회
        if (blockedUsers.contains(sender)) { // 쪽지를 보낸 유저가 차단된 유저인 경우
            Blocked blocked = blockedRepository.findByBlockingUserAndBlockedUser(receiverUser, sender)
                    .orElseThrow(() -> new BlockedException(BlockedErrorResult.BLOCKED_NOT_FOUND));

            chatList = chatRepository.findByChatRoom(userId, roomId, blocked.getCreatedDate(), pageable);  // 차단된 이후의 채팅은 조회하지 않음
            opponentIsBlocked = true;
        } else { // 쪽지를 보낸 유저와 차단관계가 없는 경우
            chatList = chatRepository.findByChatRoom(userId, roomId, pageable);
            opponentIsBlocked = false;
        }

        String opponentImage = sender.getProfileImagePath();
        return ChatDetailResponse.of(chatRoom, chatList, opponentImage, opponentIsBlocked);

    }

    /**
     * 대화방 삭제
     */
    public Long deleteChatRoom(Long userId, Long roomId) {
        chatRoomRepository.findById(roomId)
                .ifPresentOrElse(chatRoom -> {
                    if (chatRoom.getReceiver() == null) {
                        throw new ChatException(ChatErrorResult.WITHDRAWN_USER);
                    }
                    if (!Objects.equals(chatRoom.getReceiver().getId(), userId)) {
                        throw new ChatException(ChatErrorResult.FORBIDDEN_ACCESS);
                    }
                    if (chatRoom.getPartnerId() != null) {
                        chatRoomRepository.findById(chatRoom.getPartnerId())
                                .ifPresent(partnerChatRoom -> partnerChatRoom.updatePartnerId(null));
                    }
                },()-> {
                    throw new ChatException(ChatErrorResult.CHAT_ROOM_NOT_FOUND);
                });
        chatRoomRepository.deleteById(roomId);
        return roomId;
    }

    /**
     * 대화방이 있는지 검증해준 후 없으면 새로 생성해준다
     */
    private ChatRoom getOrCreateChatRoom(User sendUser, User receiveUser, Long commentId, Post post, Boolean anonymous) {
        ChatRoom chatRoom = chatRoomRepository.findByReceiverAndSenderAndPostAndAnonymous(receiveUser, sendUser, post, anonymous)
                .orElse(null);

        if (chatRoom == null) {
            chatRoom = ChatRoom.of(receiveUser, sendUser, post, anonymous); // 대화방 없으면 새로 생성
            // 댓글 작성자와 쪽지 교환이면 comment 정보도 엮여줌.
            if (commentId != null) {
                Comment comment = commentRepository.findById(commentId)
                                .orElseThrow(()-> new CommentException(CommentErrorResult.COMMENT_NOT_FOUND));
                chatRoom.updateComment(comment);
            }
            chatRoomRepository.save(chatRoom);
        }
        return chatRoom;
    }

    /**
     * 나와 상태방의 채팅방과 채팅을 생성한다
     */
    private Long createChatRoom(ChatRoomCreateRequest request, User user, Long commentId, Post post, Boolean anonymousInfo, User receiveUser) {
        ChatRoom myRoom = getOrCreateChatRoom(user, receiveUser, commentId, post, anonymousInfo);
        ChatRoom partnerRoom = getOrCreateChatRoom(receiveUser, user, commentId, post, anonymousInfo);
        myRoom.updatePartnerId(partnerRoom.getId());
        partnerRoom.updatePartnerId(myRoom.getId());

        Chat myChat = Chat.of(request.getMessage(), myRoom, receiveUser, user);
        Chat partnerChat = Chat.of(request.getMessage(), partnerRoom, receiveUser, user);
        chatRepository.save(myChat);
        return chatRepository.save(partnerChat).getId();
    }

    /**
     * 나와 상대방의 채팅을 생성한다
     */
    private Long createChat(String request, ChatRoom myRoom, User receiveUser, User user, ChatRoom partnerRoom) {
        Chat myChat = Chat.of(request, myRoom, receiveUser, user);
        Chat partnerChat = Chat.of(request, partnerRoom, receiveUser, user);
        Chat newChat = chatRepository.save(myChat);
        chatRepository.save(partnerChat);
        return newChat.getId();
    }

    /**
     * 차단한 유저 조회
     */
    private List<User> getBlockedUsers(User receiveUser) {
        return blockedRepository.findByBlockingUser(receiveUser).stream()
                .map(Blocked::getBlockedUser)
                .collect(Collectors.toList());
    }

    /**
     * 예외처리 - 채팅을 생성할 수 있는 유저인가
     */
    private void validateSaveChat(Long userId, User receiveUser) {
        if(Objects.equals(receiveUser.getNickName(), "알 수 없음")){
            throw new ChatException(ChatErrorResult.WITHDRAWN_USER);
        }

        if (Objects.equals(userId, receiveUser.getId())) {
            throw new ChatException(ChatErrorResult.CANNOT_SEND_TO_SELF);
        }
    }

    /**
     * 예외처리 - 채팅을 생성할 수 있는 유저인가 (채팅방 생성 이후)
     */
    private void validateSaveChatAfterCreateChatRoom(Long userId, ChatRoom myRoom) {
        if (myRoom.getReceiver() == null || myRoom.getSender() == null || Objects.equals(myRoom.getSender().getNickName(), "알 수 없음")) {
            throw new ChatException(ChatErrorResult.WITHDRAWN_USER);
        }

        if (!Objects.equals(myRoom.getReceiver().getId(), userId)) {
            throw new ChatException(ChatErrorResult.FORBIDDEN_ACCESS);
        }

        Long partnerUserId = myRoom.getSender().getId();
        if (Objects.equals(userId, partnerUserId)) {
            throw new ChatException(ChatErrorResult.CANNOT_SEND_TO_SELF);
        }
    }

    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 채팅방인가
     */
    private ChatRoom getChatRoom(Long roomId) {
        return  chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorResult.CHAT_ROOM_NOT_FOUND));
    }

}
