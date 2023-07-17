package FIS.iLUVit.dto.board;

import FIS.iLUVit.domain.enumtype.BoardKind;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDto {
    @NotBlank(message = "게시판 이름을 채워주세요.")
    private String boardName;
    private BoardKind boardKind;
}
