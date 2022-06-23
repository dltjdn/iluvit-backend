package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.BoardKind;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateBoardRequest {
    private String board_name;
    private BoardKind boardKind;
}
