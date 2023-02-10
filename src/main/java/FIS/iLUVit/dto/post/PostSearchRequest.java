package FIS.iLUVit.dto.post;

import FIS.iLUVit.domain.enumtype.Auth;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostSearchRequest {
    private Long center_id;
    private String input;
    private Auth auth;
}
