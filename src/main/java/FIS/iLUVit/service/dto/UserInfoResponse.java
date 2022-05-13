package FIS.iLUVit.service.dto;

import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserInfoResponse {
    private String nickname;
    private Auth auth;
}
