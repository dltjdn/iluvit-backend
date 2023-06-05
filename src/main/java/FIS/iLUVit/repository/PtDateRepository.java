package FIS.iLUVit.repository;

import FIS.iLUVit.domain.PtDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface PtDateRepository extends JpaRepository<PtDate, Long> {

    /*
        설명회 날짜 id를 파라미터로 받아서 id와 설명회 참여로 조회합니다.
     */
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.participations as participation " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch participation.parent " +
            "where ptDate.id = :id")
    Optional<PtDate> findByIdAndJoinParticipation(@Param("id") Long ptDateId);

    /*
        설명회 날짜 id를 파라미터로 받아서 아이디로 찾기 및 설명회 참여 검색합니다.
     */
    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.participations as participation " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch presentation.center " +
            "left join fetch participation.parent " +
            "where ptDate.id = :id")
    Optional<PtDate> findByIdAndJoinParticipationForSearch(@Param("id") Long ptDateId);

    /*
        설명회 날짜 id를 파라미터로 받아서 id 및 참여 웨이팅으로 조회합니다.
     */
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.waitings as waiting " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch waiting.parent " +
            "where ptDate.id = :ptDateId")
    Optional<PtDate> findByIdAndJoinWaiting(@Param("ptDateId") Long ptDateId);

    /*
        설명회 날짜 id를 파라미터로 받아서 웨이팅 및 설명회 및 센터 및 부모 ID로 조회합니다.
     */
    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.waitings as waiting " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch presentation.center " +
            "left join fetch waiting.parent " +
            "where ptDate.id = :ptDateId")
    Optional<PtDate> findByIdWithWaitingAndPresentationAndCenterAndParent(@Param("ptDateId") Long ptDateId);

    /*
        설명회 날짜 key들을 파라미터로 받아서 ID로 설명회 날짜를 삭제합니다.
     */
    @Modifying
    @Query("delete from PtDate ptdate where ptdate.id in :ptDateKeys")
    void deletePtDateByIds(@Param("ptDateKeys") Set<Long> ptDateKeysDeleteTarget);

    // 설명회 대기 들록을 위한 ptDate 정보 가져오기
    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch ptDate.waitings as waiting " +
            "where ptDate.id = :id ")
    Optional<PtDate> findByIdWith(@Param("id") Long id);
}
