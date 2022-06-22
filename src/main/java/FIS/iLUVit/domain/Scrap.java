package FIS.iLUVit.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@OnDelete(action = OnDeleteAction.CASCADE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;                    // 스크랩파일 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "scrap")
    private List<ScrapPost> scrapPosts = new ArrayList<>();

    public static Scrap createScrap(User user, String name) {
        Scrap scrap = new Scrap();
        scrap.user = user;
        scrap.name = name;
        return scrap;
    }

    public void updateScrapDirName(String name) {
        this.name = name;
    }
}
