package FIS.iLUVit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(
        name = "user_token_unique",
        columnNames = {"token", "user_id"}
)})
public class ExpoToken {

    @Id @GeneratedValue
    private Long id;
    private String token;
    private String deviceId;  // 기기 고유 id
    private Boolean active = true; // 엑스포 토큰 활성화, 비활성화 여부 ( 비활성화 된 것은 주기적으로 삭제 )
    private Boolean accept; // 알림 수신 O, X

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public ExpoToken(Long id, String token, Boolean accept, User user, String deviceId) {
        this.id = id;
        this.token = token;
        this.accept = accept;
        this.user = user;
        this.deviceId = deviceId;
    }

    public void modifyAcceptStatus(Boolean accept) {
        this.accept = accept;
    }

}
