package FIS.iLUVit.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCheckDuplicateNicknameRequest {
    private  String nickname;
}
