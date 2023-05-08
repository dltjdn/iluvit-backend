package FIS.iLUVit.domain;

import lombok.Getter;

import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

@Getter
@MappedSuperclass
public class BaseImageEntity extends BaseEntity{

    @Lob
    protected String infoImagePath;
    protected String profileImagePath;
    protected Integer imgCnt;

    public void updateInfoImagePath(Integer imgCnt, String infoImagePath) {
        this.imgCnt = imgCnt;
        this.infoImagePath = infoImagePath;
    }

    public void updateProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}
