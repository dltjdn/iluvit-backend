package FIS.iLUVit.domain;

import FIS.iLUVit.domain.embeddable.Theme;
import lombok.Getter;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
public class Parent extends User{
    @OneToMany(mappedBy = "parent")
    private List<Child> children;

    @Embedded
    private Theme theme;                    // 테마 (학부모 관심사)

    private Integer interestAge;            // 관심나이
}
