package FIS.iLUVit.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class PostRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;                   // 게시글 제목
    @Lob
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;                 // 게시글 내용

    @NotNull(message = "익명 여부 필요")
    private Boolean anonymous;              // 익명

    @NotNull(message = "게시판 아이디 필요")
    private Long board_id;                  // 게시판 아이디
}
