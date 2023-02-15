package FIS.iLUVit.dto.parent;

import FIS.iLUVit.domain.Parent;
import lombok.Getter;


@Getter
public class ParentDto {

    private String phoneNumber;         // 핸드폰 번호
    private String emailAddress;        // 이메일
    private String name;                // 잔짜 이름

    public ParentDto(Parent parent) {
        phoneNumber = parent.getPhoneNumber();
        emailAddress = parent.getEmailAddress();
        name = parent.getName();
    }
}
