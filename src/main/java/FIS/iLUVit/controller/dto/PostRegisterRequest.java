package FIS.iLUVit.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;

@Data
@NoArgsConstructor
public class PostRegisterRequest {
    private String title;                   // 게시글 제목
    @Lob
    private String content;                 // 게시글 내용
    private Boolean anonymous;              // 익명

    private Long board_id;                  // 게시판 아이디
}
