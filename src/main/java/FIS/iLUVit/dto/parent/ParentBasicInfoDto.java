package FIS.iLUVit.dto.parent;

import FIS.iLUVit.dto.user.UserBasicInfoDto;
import FIS.iLUVit.domain.enumtype.Auth;
import lombok.Getter;

@Getter
public class ParentBasicInfoDto extends UserBasicInfoDto {

    public ParentBasicInfoDto(Long id, String nickName, Auth auth) {

        super(id, nickName, auth);
    }
}
