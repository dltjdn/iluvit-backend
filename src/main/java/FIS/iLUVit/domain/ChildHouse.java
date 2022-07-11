package FIS.iLUVit.domain;

import FIS.iLUVit.domain.embeddable.*;
import FIS.iLUVit.domain.enumtype.KindOf;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
public class ChildHouse extends Center{

    @Builder(builderMethodName = "cBuilder")
    public ChildHouse(Long id, String name, String owner, String director, String estType, String status, String estDate, String tel, String homepage, String startTime, String endTime, Integer minAge, Integer maxAge, String address, String zipcode, Area area, Double longitude, Double latitude, String offerService, Integer maxChildCnt, Integer curChildCnt, LocalDate updateDate, Boolean signed, Boolean recruit, Integer waitingNum, String introText, Integer imgCnt, Integer videoCnt, Integer score, String addInfo, String program, KindOf kindOf, ClassInfo classInfo, TeacherInfo teacherInfo, CostInfo costInfo, BasicInfra basicInfra, Theme theme, OtherInfo otherInfo) {
        super(id, name, owner, director, estType, status, estDate, tel, homepage, startTime, endTime, minAge, maxAge, address, zipcode, area, longitude, latitude, offerService, maxChildCnt, curChildCnt, updateDate, signed, recruit, waitingNum, introText, imgCnt, videoCnt, score, addInfo, program, kindOf, classInfo, teacherInfo, costInfo, basicInfra, theme, otherInfo);
    }
}
