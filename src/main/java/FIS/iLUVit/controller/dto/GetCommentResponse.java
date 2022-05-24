package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCommentResponse {
    private String nickname;
    private String content;
    private Integer heartCnt;
    private LocalDate date;
    private LocalTime time;
    private Boolean anonymous;

    private List<Answer> answers;

    public GetCommentResponse(Comment comment) {
        this.nickname = comment.getUser().getNickName();
        this.content = comment.getContent();
        this.heartCnt = comment.getCommentHearts().size();
        this.date = comment.getDate();
        this.time = comment.getTime();
        this.anonymous = comment.getAnonymous();
        this.answers = comment.getSubComments().stream()
                .map(c -> new Answer(c))
                .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Answer {
        private String nickname;
        private String content;
        private Integer heartCnt;
        private LocalDate date;
        private LocalTime time;
        private Boolean anonymous;

        public Answer(Comment comment) {
            this.nickname = comment.getUser().getNickName();
            this.content = comment.getContent();
            this.heartCnt = comment.getCommentHearts().size();
            this.date = comment.getDate();
            this.time = comment.getTime();
            this.anonymous = comment.getAnonymous();
        }
    }
}

