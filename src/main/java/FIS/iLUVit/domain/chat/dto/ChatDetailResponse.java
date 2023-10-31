package FIS.iLUVit.domain.chat.dto;

import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.chat.domain.Chat;
import FIS.iLUVit.domain.chat.domain.ChatRoom;
import FIS.iLUVit.domain.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ChatDetailResponse {
    @JsonProperty("room_id")
    private Long roomId;
    @JsonProperty("center_id")
    private Long centerId;
    private String centerName; // centerId null이면 모두의 이야기
    @JsonProperty("board_id")
    private Long boardId;
    private String boardName;
    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("comment_id")
    private Long commentId;

    @JsonProperty("my_id")
    private Long myId;
    private Boolean anonymous;
    @JsonProperty("opponent_id")
    private Long opponentId;

    @JsonProperty("opponent_nickname")
    private String opponentNickname;

    @JsonProperty("opponent_image")
    private String opponentImage;
    private Boolean opponentIsBlocked; // 쪽지를 보낸 유저가 차단된 유저인지 판단

    private Slice<ChatInfo> chatList;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(access = AccessLevel.PRIVATE)
    public static class ChatInfo {
        @JsonProperty("chat_id")
        private Long chatId;

        @JsonProperty("sender_id")
        private Long senderId;
        @JsonProperty("receiver_id")
        private Long receiverId;
        private LocalDate date;
        private LocalTime time;
        private String message;

        public static ChatInfo of(Chat chat){
            return ChatInfo.builder()
                    .chatId(chat.getId())
                    .senderId(chat.getSender().getId())
                    .receiverId(chat.getReceiver().getId())
                    .date(chat.getDate())
                    .time(chat.getTime())
                    .message(chat.getMessage())
                    .build();
        }

    }

    public static ChatDetailResponse of(ChatRoom chatRoom, Slice<Chat> chatList, String opponentImage, Boolean opponentIsBlocked){
        ChatDetailResponseBuilder builder = ChatDetailResponse.builder()
                .roomId(chatRoom.getId())
                .chatList(chatList.map(ChatInfo::of))
                .anonymous(chatRoom.getAnonymous())
                .opponentIsBlocked(opponentIsBlocked)
                .opponentImage(opponentImage);

        if (chatRoom.getPost() != null) {
            builder.boardId(chatRoom.getPost().getBoard().getId())
                    .boardName(chatRoom.getPost().getBoard().getName())
                    .postId(chatRoom.getPost().getId());

            if(chatRoom.getPost().getBoard().getCenter() != null){
                builder.centerId(chatRoom.getPost().getBoard().getCenter().getId())
                        .centerName(chatRoom.getPost().getBoard().getCenter().getName()+ "의 이야기");
            }else{
                builder.centerName("모두의 이야기");
            }
        }

        if (chatRoom.getComment() != null) {
            builder.commentId(chatRoom.getComment().getId());
        }

        if (chatRoom.getReceiver() != null) {
            builder.myId(chatRoom.getReceiver().getId());
        }

        if (chatRoom.getSender() != null) {
            builder.opponentId(chatRoom.getSender().getId())
                    .opponentNickname(chatRoom.getSender().getNickName());
        }

        return builder.build();
    }



}
