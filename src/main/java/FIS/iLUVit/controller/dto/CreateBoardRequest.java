package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.BoardKind;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBoardRequest {
    @NotBlank(message = "게시판 이름을 채워주세요.")
    private String board_name;
    @NotBlank(message = "게시판 종류를 설정해주세요.")
    private BoardKind boardKind;
}
