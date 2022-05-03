package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;

import javax.persistence.*;

@Entity
public class Teacher extends User{

    @Enumerated(EnumType.STRING)
    private Auth auth;                      // 교사 권한

    @Enumerated(EnumType.STRING)
    private Approval approval;              // 교사 승인 여부

    @ManyToOne
    @JoinColumn(name = "center_id")
    private Center center;

}
