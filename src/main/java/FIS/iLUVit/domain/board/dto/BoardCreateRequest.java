package FIS.iLUVit.domain.board.dto;

import FIS.iLUVit.domain.board.domain.BoardKind;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
@Getter
@NoArgsConstructor
public class BoardCreateRequest {
    @NotBlank(message = "게시판 이름을 채워주세요.")
    @JsonProperty("board_name")
    private String boardName;
    private BoardKind boardKind;
}
