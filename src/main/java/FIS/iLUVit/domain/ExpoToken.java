package FIS.iLUVit.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ExpoToken {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String token;

    private Boolean accept; // 푸쉬 알림 동의 여부

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

    @PrePersist
    public void prePersist() {
        this.accept = this.accept == null && false;
    }

    public void modifyAcceptStatus(Boolean accept) {
        this.accept = accept;
    }

}
