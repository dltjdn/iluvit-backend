package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.Comment;
import FIS.iLUVit.domain.User;
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
    private Long id;
    private Long writer_id;
    private String nickname;
    private String content;
    private Integer heartCnt;
    private LocalDate date;
    private LocalTime time;
    private Boolean anonymous;

    private List<Answer> answers;

    public GetCommentResponse(Comment comment) {
        this.id = comment.getId();
        User writer = comment.getUser();
        if (writer != null) {
            this.writer_id = writer.getId();
            this.nickname = writer.getNickName();
            this.heartCnt = comment.getCommentHearts().size();
            this.anonymous = comment.getAnonymous();
        }
        this.content = comment.getContent();
        this.date = comment.getDate();
        this.time = comment.getTime();
        this.answers = comment.getSubComments().stream()
                .map(c -> new Answer(c))
                .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Answer {
        private Long id;
        private Long writer_id;
        private String nickname;
        private String content;
        private Integer heartCnt;
        private LocalDate date;
        private LocalTime time;
        private Boolean anonymous;

        public Answer(Comment comment) {
            this.id = comment.getId();
            this.writer_id = comment.getUser().getId();
            this.nickname = comment.getUser().getNickName();
            this.content = comment.getContent();
            this.heartCnt = comment.getCommentHearts().size();
            this.date = comment.getDate();
            this.time = comment.getTime();
            this.anonymous = comment.getAnonymous();
        }
    }
}

