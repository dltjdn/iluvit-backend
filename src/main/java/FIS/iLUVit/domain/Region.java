package FIS.iLUVit.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Region {
    @Id
    @GeneratedValue
    private Long id;

    private String sidoName;
    private String sidoCode;
    private String sigunguName;
    private String sigunguCode;

}