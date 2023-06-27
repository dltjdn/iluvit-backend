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
        설명회 날짜 id에 해당하는 설명회 날짜 객체를 가져오고 설명회의 부모 객체를 포함하여 설명회 날짜로 불러옵니다.
     */
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.participations as participation " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch participation.parent " +
            "where ptDate.id = :id")
    Optional<PtDate> findByIdAndJoinParticipation(@Param("id") Long ptDateId);

    /*
        설명회 날짜 id에 해당하는 설명회 날짜 객체를 가져오고 설명회의 시설 객체를 포함하여 설명회 날짜로 불러옵니다.
     */
    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.participations as participation " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch presentation.center " +
            "left join fetch participation.parent " +
            "where ptDate.id = :id")
    Optional<PtDate> findByIdAndJoinParticipationForSearch(@Param("id") Long ptDateId);

    /*
        설명회 날짜 id에 해당하는 설명회 날짜 객체를 가져오고 waiting의 부모 객체를 포함하여 설명회 날짜로 불러옵니다.
     */
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.waitings as waiting " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch waiting.parent " +
            "where ptDate.id = :ptDateId")
    Optional<PtDate> findByIdAndJoinWaiting(@Param("ptDateId") Long ptDateId);

    /*
        설명회 날짜 id에 해당하는 설명회 날짜 객체를 가져오고 waiting의 parent 객체를 포함하여 설명회 날짜로 불러옵니다.
     */
    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.waitings as waiting " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch presentation.center " +
            "left join fetch waiting.parent " +
            "where ptDate.id = :ptDateId")
    Optional<PtDate> findByIdWithWaitingAndPresentationAndCenterAndParent(@Param("ptDateId") Long ptDateId);

    /*
        ptDateKeysDeleteTarget에 해당하는 id를 가진 PtDate들을 삭제합니다.
     */
    @Modifying
    @Query("delete from PtDate ptdate where ptdate.id in :ptDateKeys")
    void deletePtDateByIds(@Param("ptDateKeys") Set<Long> ptDateKeysDeleteTarget);

    /*
        설명회 대기 등록을 위한 ptDate 정보를 가져옵니다.
     */
    @Query("select distinct ptDate from PtDate ptDate " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch ptDate.waitings as waiting " +
            "where ptDate.id = :id ")
    Optional<PtDate> findByIdWith(@Param("id") Long id);
}
