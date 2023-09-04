package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.dto.data.ChildHouseInfoResponse;
import FIS.iLUVit.dto.data.KindergartenGeneralResponse;
import FIS.iLUVit.dto.data.KindergartenTeacherResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CenterRepository extends JpaRepository<Center, Long>, CenterRepositoryCustom {

    /**
     * 아이디와 원장 가입 유무로 시설을 조회합니다
     */
    Optional<Center> findByIdAndSigned(Long centerId, Boolean signed);

    /**
     * 시설 아이디 리스트와 종류로 시설을 조회합니다 ( 점수 기준으로 내림차순 후 아이디 기준으로 오름차순 )
     */
    List<Center> findByIdInAndKindOfOrderByScoreDescIdAsc(List<Long> centerIds, KindOf kindOf);

    /**
     * 시설 아이디 리스트로 시설을 조회합니다 ( 점수 기준으로 내림차순 후 아이디 기준으로 오름차순 )
     */
    List<Center> findByIdInOrderByScoreDescIdAsc(List<Long> centerIds);

    /**
     * 주어진 데이터로 어린이집 정보를 업데이트합니다
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Center c SET c.estType = :#{#response.estType}, " +
            "c.status= :#{#response.status}, c.owner= :#{#response.owner}, c.zipcode= :#{#response.zipcode}, " +
            "c.homepage= :#{#response.homepage}, c.maxChildCnt= :#{#response.maxChildCnt}, c.curChildCnt= :#{#response.curChildCnt}, " +
            "c.basicInfra.hasBus= :hasBus, c.basicInfra.hasPlayground= :hasPlayground, " +
            "c.basicInfra.hasCCTV= :hasCCTV, c.basicInfra.cctvCnt= :#{#response.cctvCnt}, " +
            "c.classInfo.class_0= :#{#response.class_0}, c.classInfo.class_1= :#{#response.class_1}, c.classInfo.class_2= :#{#response.class_2}, " +
            "c.classInfo.class_3= :#{#response.class_3}, c.classInfo.class_4= :#{#response.class_4}, c.classInfo.class_5 = :#{#response.class_5}, " +
            "c.classInfo.child_0= :#{#response.child_0}, c.classInfo.child_1= :#{#response.child_1}, c.classInfo.child_2= :#{#response.child_2}, " +
            "c.classInfo.child_3= :#{#response.child_3}, c.classInfo.child_4= :#{#response.child_4}, c.classInfo.child_5= :#{#response.child_5}, " +
            "c.classInfo.child_spe = :#{#response.child_spe}, c.teacherInfo.totalCnt = :totalCnt, c.teacherInfo.dur_1= :dur_1, " +
            "c.teacherInfo.dur12= :dur12, c.teacherInfo.dur24 = :dur24, c.teacherInfo.dur46= :dur46, c.teacherInfo.dur6_= :dur6_ " +
            "WHERE c.name = :#{#response.centerName} AND c.area.sido = :#{#response.sido} AND c.area.sigungu = :#{#response.sigungu}")
    void updateChildHouse(@Param("response") ChildHouseInfoResponse response
            , @Param("hasBus") Boolean hasBus
            , @Param("hasPlayground") Boolean hasPlayground
            , @Param("hasCCTV") Boolean hasCCTV
            , @Param("totalCnt") Integer totalCnt
            , @Param("dur_1") Integer dur_1
            , @Param("dur12") Integer dur12
            , @Param("dur24") Integer dur24
            , @Param("dur46") Integer dur46
            , @Param("dur6_") Integer dur6_
    );

    /**
     * 해당 시설의 기본 정보를 업데이트합니다
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Center c SET c.estType = :#{#response.estType}, c.estDate = :#{#response.estDate}, " +
            "c.owner= :#{#response.owner}, c.director= :#{#response.director}, c.homepage= :#{#response.homepage}, " +
            "c.startTime= :#{#response.startTime}, c.endTime= :#{#response.endTime}, c.maxChildCnt= :#{#response.maxChildCnt}, " +
            "c.classInfo.class_3= :#{#response.class_3}, c.classInfo.class_4= :#{#response.class_4}, c.classInfo.class_5 = :#{#response.class_5}, " +
            "c.classInfo.child_3= :#{#response.child_3}, c.classInfo.child_4= :#{#response.child_4}, c.classInfo.child_5= :#{#response.child_5}, " +
            "c.classInfo.child_spe = :#{#response.child_spe} " +
            "WHERE c.name = :#{#response.centerName} AND c.area.sido = :sido AND c.area.sigungu = :sigungu")
    void updateKindergartenForGeneral(@Param("response")KindergartenGeneralResponse response, @Param("sido") String sido, @Param("sigungu") String sigungu);

    /**
     * 해당 시설의 선생님 정보를 업데이트합니다
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Center c SET c.teacherInfo.totalCnt = :#{#response.totalCnt}, c.teacherInfo.dur_1= :#{#response.dur_1}, " +
            "c.teacherInfo.dur12= :#{#response.dur12}, c.teacherInfo.dur24 = :#{#response.dur24}, " +
            "c.teacherInfo.dur46= :#{#response.dur46}, c.teacherInfo.dur6_= :#{#response.dur6_} " +
            "WHERE c.name = :#{#response.centerName} AND c.area.sido = :sido AND c.area.sigungu = :sigungu")
    void updateKindergartenForTeacher(@Param("response") KindergartenTeacherResponse response, @Param("sido") String sido, @Param("sigungu") String sigungu);

    /**
     * 해당 시설의 건축년도 정보를 업데이트합니다
     */
    @Modifying
    @Query("UPDATE Center c SET c.basicInfra.buildingYear = :buildingYear " +
            "WHERE c.name = :name AND c.area.sido = :sido AND c.area.sigungu = :sigungu")
    void updateCenterBuildingYear(@Param("name") String name, @Param("buildingYear") Integer buildingYear
            , @Param("sido") String sido, @Param("sigungu") String sigungu);

    /**
     * 해당 시설의 체육시설 유무 정보를 업데이트합니다
     */
    @Modifying
    @Query("UPDATE Center c SET c.basicInfra.hasPhysics = :hasPhysics " +
            "WHERE c.name = :name AND c.area.sido = :sido AND c.area.sigungu = :sigungu")
    void updateCenterPhysics(@Param("name") String name, @Param("hasPhysics") Boolean hasPhysics
            , @Param("sido") String sido, @Param("sigungu") String sigungu);

    /**
     * 해당 시설의 통학버스 정보를 업데이트 합니다
     */
    @Modifying
    @Query("UPDATE Center c SET c.basicInfra.hasBus = :hasBus ,c.basicInfra.busCnt = :busCnt " +
            "WHERE c.name = :name AND c.area.sido = :sido AND c.area.sigungu = :sigungu")
    void updateCenterBus(@Param("name") String name, @Param("hasBus") Boolean hasBus, @Param("busCnt") Integer busCnt
            , @Param("sido") String sido, @Param("sigungu") String sigungu);

    /**
     * 해당 시설의 CCTV 정보를 업데이트합니다
     */
    @Modifying
    @Query("UPDATE Center c SET c.basicInfra.hasCCTV = :hasCCTV ,c.basicInfra.cctvCnt = :cctvCnt " +
            "WHERE c.name = :name AND c.area.sido = :sido AND c.area.sigungu = :sigungu")
    void updateCenterCCTV(@Param("name") String name, @Param("hasCCTV") Boolean hasCCTV, @Param("cctvCnt") Integer cctvCnt
            , @Param("sido") String sido, @Param("sigungu") String sigungu);

}
