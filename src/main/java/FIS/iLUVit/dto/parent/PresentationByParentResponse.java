package FIS.iLUVit.dto.parent;

import FIS.iLUVit.domain.Parent;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class PresentationByParentResponse {

    private String phoneNumber;         // 핸드폰 번호
    private String emailAddress;        // 이메일
    private String name;                // 잔짜 이름

    public PresentationByParentResponse(Parent parent) {
        phoneNumber = parent.getPhoneNumber();
        emailAddress = parent.getEmailAddress();
        name = parent.getName();
    }

    public static PresentationByParentResponse of(Parent parent){
        return PresentationByParentResponse.builder()
                .phoneNumber(parent.getPhoneNumber())
                .emailAddress(parent.getEmailAddress())
                .name(parent.getName())
                .build();
    }
}
