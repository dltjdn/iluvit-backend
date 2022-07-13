package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.AuthKind;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"phoneNum", "authKind"}
                )
        }
)
public class AuthNumber extends BaseEntity{

    @Id @GeneratedValue
    private Long id;
    private String phoneNum;        // 핸드폰 번호
    private String authNum;         // 인증번호
    private LocalDateTime authTime; // 인증 완료 시간
    @Enumerated(EnumType.STRING)
    private AuthKind authKind;

    @Builder
    public AuthNumber(Long id, String phoneNum, String authNum, AuthKind authKind) {
        this.id = id;
        this.phoneNum = phoneNum;
        this.authNum = authNum;
        this.authKind = authKind;
    }

    public static AuthNumber createAuthNumber(String phoneNum, String authNum, AuthKind authKind) {
        return AuthNumber.builder()
                .phoneNum(phoneNum)
                .authNum(authNum)
                .authKind(authKind)
                .build();
    }

    public void AuthComplete(){
        this.authTime = LocalDateTime.now();
    }
}
