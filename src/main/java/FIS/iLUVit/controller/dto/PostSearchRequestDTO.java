package FIS.iLUVit.controller.dto;

import FIS.iLUVit.domain.enumtype.Auth;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostSearchRequestDTO {
    private Long centerId;
    private String input;
    private Auth auth;
}
