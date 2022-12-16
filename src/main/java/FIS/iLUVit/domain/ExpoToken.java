package FIS.iLUVit.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(
        name = "user_token_unique",
        columnNames = {"token", "user_id"}
)})
public class ExpoToken {

    @Id @GeneratedValue
    private Long id;

    private String token;

    private Boolean accept; // 알림 수신 O, X

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public ExpoToken(Long id, String token, Boolean accept, User user) {
        this.id = id;
        this.token = token;
        this.accept = accept;
        this.user = user;
    }

    public void modifyAcceptStatus(Boolean accept) {
        this.accept = accept;
    }

}
