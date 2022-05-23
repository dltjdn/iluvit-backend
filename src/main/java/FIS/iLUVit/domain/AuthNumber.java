package FIS.iLUVit.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthNumber extends BaseEntity{

    @Id @GeneratedValue
    private Long id;
    private String phoneNum;    // 핸드폰 번호
    private String authNum;     // 인증번호

    public AuthNumber(String phoneNum, String authNum) {
        this.phoneNum = phoneNum;
        this.authNum = authNum;
    }
}
