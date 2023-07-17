package FIS.iLUVit.dto.parent;

import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.security.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginParentResponse extends UserDto {

    public LoginParentResponse(Parent parent) {
        super(parent);
    }
}

