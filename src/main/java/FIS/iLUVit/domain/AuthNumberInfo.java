package FIS.iLUVit.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthNumberInfo extends BaseEntity{

    @Id @GeneratedValue
    private Long id;
    private String phoneNum;        // 핸드폰 번호
    private String authNum;         // 인증번호
    private LocalDateTime authTime; // 인증 완료 시간

    public AuthNumberInfo(String phoneNum, String authNum) {
        this.phoneNum = phoneNum;
        this.authNum = authNum;
    }
}
