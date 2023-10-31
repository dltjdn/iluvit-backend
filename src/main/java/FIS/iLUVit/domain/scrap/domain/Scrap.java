package FIS.iLUVit.domain.scrap.domain;

import FIS.iLUVit.domain.common.domain.BaseEntity;
import FIS.iLUVit.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;                    // 스크랩파일 이름
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "scrap")
    private List<ScrapPost> scrapPosts = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Scrap(User user, String name, Boolean isDefault) {
        this.user = user;
        this.name = name;
        this.isDefault = isDefault;
    }

    public static Scrap of(User user, String name) {
        return Scrap.builder()
                .user(user)
                .name(name)
                .isDefault(false)
                .build();
    }

    public static Scrap from(User user) {
        return Scrap.builder()
                .user(user)
                .name("기본폴더")
                .isDefault(true)
                .build();
    }

    public void updateScrapDirName(String name) {
        this.name = name;
    }
}