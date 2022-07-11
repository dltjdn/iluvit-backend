package FIS.iLUVit;

import FIS.iLUVit.domain.AuthNumber;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.AuthKind;

public class Creator {

    public static Parent createParent(String phoneNum){
        return Parent.builder()
                .nickName("asd")
                .loginId("asd")
                .password("asd")
                .phoneNumber(phoneNum)
                .hasProfileImg(false)
                .emailAddress("asd@asd")
                .name("asd")
                .address("asd")
                .detailAddress("asd")
                .build();
    }

}
