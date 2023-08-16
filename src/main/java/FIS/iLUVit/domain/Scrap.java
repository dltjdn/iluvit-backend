package FIS.iLUVit.domain;

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

    @Builder
    private Scrap(Long id, User user, String name, Boolean isDefault) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.isDefault = isDefault;
    }

    public static Scrap createScrap(User user, String name) {
        Scrap scrap = new Scrap();
        scrap.user = user;
        scrap.name = name;
        scrap.isDefault = false;
        return scrap;
    }

    public static Scrap createDefaultScrap(User user) {
        Scrap scrap = new Scrap();
        scrap.user = user;
        scrap.name = "기본폴더";
        scrap.isDefault = true;
        return scrap;
    }

    public void updateScrapDirName(String name) {
        this.name = name;
    }
}