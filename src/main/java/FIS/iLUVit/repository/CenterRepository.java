package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.enumtype.KindOf;
import org.springframework.data.jpa.repository.JpaRepository;

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

}