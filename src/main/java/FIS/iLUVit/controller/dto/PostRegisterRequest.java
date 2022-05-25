package FIS.iLUVit.controller.dto;

import lombok.Data;

import javax.persistence.Lob;

@Data
public class PostRegisterRequest {
    private String title;                   // 게시글 제목
    @Lob
    private String content;                 // 게시글 내용
    private Boolean anonymous;              // 익명

    private Long board_id;                  // 게시판 아이디
}
