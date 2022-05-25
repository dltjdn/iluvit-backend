package FIS.iLUVit.service.dto;

import FIS.iLUVit.domain.Parent;
import lombok.Data;

import javax.persistence.Column;

@Data
public class ParentInfoForDirectorDto {

    private String phoneNumber;         // 핸드폰 번호
    private String emailAddress;        // 이메일
    private String name;                // 잔짜 이름

    public ParentInfoForDirectorDto(Parent parent) {
        phoneNumber = parent.getPhoneNumber();
        emailAddress = parent.getEmailAddress();
        name = parent.getName();
    }
}
