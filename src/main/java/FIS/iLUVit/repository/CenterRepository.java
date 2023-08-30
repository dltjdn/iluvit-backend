package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.enumtype.KindOf;
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
     * 해당 시도, 시군구, 시설명으로 시설을 조회합니다
     */
    List<Center> findByNameAndAreaSidoAndAreaSigungu(String Name, String sido, String sigungu);

    /**
     * 해당 시설의 건축년도 정보를 업데이트합니다
     */
    @Modifying
    @Query("UPDATE Center c SET c.basicInfra.buildingYear = :buildingYear WHERE c.name = :name")
    void updateCenterBuildingYear(@Param("name") String name, @Param("buildingYear") Integer buildingYear);

    /**
     * 해당 시설의 체육시설 유무 정보를 업데이트합니다
     */
    @Modifying
    @Query("UPDATE Center c SET c.basicInfra.hasPhysics = :hasPhysics WHERE c.name = :name")
    void updateCenterPhysics(@Param("name") String name, @Param("hasPhysics") Boolean hasPhysics);

    /**
     * 해당 시설의 통학버스 정보를 업데이트 합니다
     */
    @Modifying
    @Query("UPDATE Center c SET c.basicInfra.hasBus = :hasBus, c.basicInfra.busCnt = :busCnt WHERE c.name = :name")
    void updateCenterBus(@Param("name") String name, @Param("hasBus") Boolean hasBus, @Param("busCnt") Integer busCnt);

    /**
     * 해당 시설의 CCTV 정보를 업데이트합니다
     */
    @Modifying
    @Query("UPDATE Center c SET c.basicInfra.hasCCTV = :hasCCTV, c.basicInfra.cctvCnt = :cctvCnt WHERE c.name = :name")
    void updateCenterCCTV(@Param("name") String name, @Param("hasCCTV") Boolean hasCCTV, @Param("cctvCnt") Integer cctvCnt);

}