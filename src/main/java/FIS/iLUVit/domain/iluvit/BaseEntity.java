package FIS.iLUVit.domain.iluvit;


import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class BaseEntity {

    @Column(updatable = false)
    protected LocalDateTime createdDate;

    protected LocalDateTime updatedDate;

    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate
    public void preUpdate(){
        updatedDate = LocalDateTime.now();
    }

    public BaseEntity setCreatedDateForTest(LocalDateTime localDateTime) {
        this.createdDate = localDateTime;
        return this;
    }

    public BaseEntity setUpdateDateForTest(LocalDateTime localDateTime) {
        this.updatedDate = localDateTime;
        return this;
    }
}
