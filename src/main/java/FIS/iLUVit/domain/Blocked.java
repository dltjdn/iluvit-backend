package FIS.iLUVit.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blocked extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocking_user_id")
    private User blockingUser;              // 차단한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_user_id")
    private User blockedUser;                // 차단 당한 사람

    public static Blocked createBlocked(User blockingUser, User blockedUser){
        return Blocked.builder()
                .blockingUser(blockingUser)
                .blockedUser(blockedUser)
                .build();
    }

}
