package FIS.iLUVit.domain.center.domain;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
public class Kindergarten extends Center {

    public static Kindergarten createKindergarten(String name, String owner, String director, String estType, String status, String estDate, String tel, String homepage, String startTime, String endTime, Integer minAge, Integer maxAge, String address,
                                      String zipcode, Area area, Double longitude, Double latitude, String offerService, Integer maxChildCnt, Integer curChildCnt, LocalDate updateDate, Boolean signed, Boolean recruit, Integer waitingNum, String introText,
                                      Integer imgCnt, Integer videoCnt, String kindOf, ClassInfo classInfo, TeacherInfo teacherInfo, CostInfo costInfo, BasicInfra basicInfra, Theme theme, OtherInfo otherInfo) {
        Kindergarten center = new Kindergarten();
        center.name = name;
        center.owner = owner;
        center.director = director;
        center.estType = estType;
        center.status = status;
        center.estDate = estDate;
        center.tel = tel;
        center.homepage = homepage;
        center.startTime = startTime;
        center.endTime = endTime;
        center.minAge = minAge;
        center.maxAge = maxAge;
        center.address = address;
        center.zipcode = zipcode;
        center.area = area;
        center.longitude = longitude;
        center.latitude = latitude;
        center.offerService = offerService;
        center.maxChildCnt = maxChildCnt;
        center.curChildCnt = curChildCnt;
        center.updateDate = updateDate;
        center.signed = signed;
        center.recruit = recruit;
        center.waitingNum = waitingNum;
        center.introText = introText;
        center.imgCnt = imgCnt;
        center.videoCnt = videoCnt;
        center.classInfo = classInfo;
        center.teacherInfo = teacherInfo;
        center.costInfo = costInfo;
        center.basicInfra = basicInfra;
        center.theme = theme;
        center.otherInfo = otherInfo;
        return center;
    }

    @Builder(builderMethodName = "kBuilder")
    public Kindergarten(Long id, String name, String owner, String director, String estType, String status, String estDate, String tel, String homepage, String startTime, String endTime, Integer minAge, Integer maxAge, String address, String addressDetail, String zipcode, Area area, Double longitude, Double latitude, String offerService, Integer maxChildCnt, Integer curChildCnt, LocalDate updateDate, Boolean signed, Boolean recruit, Integer waitingNum, String introText, Integer imgCnt, Integer videoCnt, Integer score, String addInfo, String program, KindOf kindOf, ClassInfo classInfo, TeacherInfo teacherInfo, CostInfo costInfo, BasicInfra basicInfra, Theme theme, OtherInfo otherInfo, String infoImagePath, String profileImagePath) {
        super(id, name, owner, director, estType, status, estDate, tel, homepage, startTime, endTime, minAge, maxAge, address, addressDetail, zipcode, area, longitude, latitude, offerService, maxChildCnt, curChildCnt, updateDate, signed, recruit, waitingNum, introText, imgCnt, videoCnt, score, addInfo, program, kindOf, classInfo, teacherInfo, costInfo, basicInfra, theme, otherInfo, infoImagePath, profileImagePath);
    }

}
