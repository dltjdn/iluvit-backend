package FIS.iLUVit.repository;

import FIS.iLUVit.dto.parent.ParticipationListDto;
import FIS.iLUVit.domain.Parent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    /*
        부모 id를 파라미터로 받아서 아이들과 함께 부모를 조회합니다.
     */
    @Query("select distinct p " +
            "from Parent p " +
            "left join fetch p.children c " +
            "left join fetch c.center " +
            "where p.id = :id")
    Optional<Parent> findWithChildren(@Param("id") Long id);

    /*
        닉네임으로 부모를 조회합니다.
     */
    Optional<Parent> findByNickName(String nickname);

    /*
        사용자 id를 파라미터로 받아서 나의 설명회를 조회합니다.
     */
    @Query("select parent " +
            "from Parent parent " +
            "left join fetch parent.participations as participation " +
            "left join fetch participation.ptDate as ptDate " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch presentation.center as center " +
            "where parent.id = :userId")
    Parent findMyParticipation(@Param("userId") Long userId);

    /*
        사용자 id를 파라미터로 받아서 나의 웨이팅을 조회합니다.
     */
    @Query("select parent " +
            "from Parent parent " +
            "left join fetch parent.waitings as waiting " +
            "left join fetch waiting.ptDate as ptDate " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch presentation.center as center " +
            "where parent.id = :userId")
    Parent findMyWaiting(@Param("userId") Long userId);

    /*
        사용자 id를 파라미터로 받아서 나의 설명회 참여를 조회합니다.
     */
    @Query("select new FIS.iLUVit.dto.parent.ParticipationListDto(" +
            "parent.id, participation.id, ptDate.id, presentation.id, center.id, ptDate.date, ptDate.time, center.profileImagePath, presentation.place, presentation.content, center.name, center.tel, center.address, center.addressDetail, ptDate.ablePersonNum, ptDate.participantCnt, participation.status" +
            ") " +
            "from Parent parent " +
            "join parent.participations as participation " +
            "join participation.ptDate as ptDate " +
            "join ptDate.presentation as presentation " +
            "join presentation.center as center " +
            "where parent.id = :userId and participation.status = FIS.iLUVit.domain.enumtype.Status.JOINED")
    Slice<ParticipationListDto> findMyJoinParticipation(@Param("userId") Long userId, Pageable pageable);

    /*
        사용자 id를 파라미터로 받아서 나의 설명회 취소를 조회합니다.
     */
    @Query("select new FIS.iLUVit.dto.parent.ParticipationListDto(" +
            "parent.id, participation.id, ptDate.id, presentation.id, center.id, ptDate.date, ptDate.time, center.profileImagePath, presentation.place, presentation.content, center.name, center.tel, center.address, center.addressDetail, ptDate.ablePersonNum, ptDate.participantCnt, participation.status" +
            ") " +
            "from Parent parent " +
            "join parent.participations as participation " +
            "join participation.ptDate as ptDate " +
            "join ptDate.presentation as presentation " +
            "join presentation.center as center " +
            "where parent.id = :userId and participation.status = FIS.iLUVit.domain.enumtype.Status.CANCELED")
    Slice<ParticipationListDto> findMyCancelParticipation(@Param("userId") Long userId, Pageable pageable);

    /*
        사용자 id를 파라미터로 받아서 나의 웨이팅을 조회합니다.
     */
    @Query("select new FIS.iLUVit.dto.parent.ParticipationListDto(" +
            "parent.id, waiting.id, ptDate.id, presentation.id, center.id, ptDate.date, ptDate.time, center.profileImagePath, presentation.place, presentation.content, center.name, center.tel, center.address, center.addressDetail, ptDate.ablePersonNum, ptDate.participantCnt" +
            ") " +
            "from Parent parent " +
            "join parent.waitings as waiting " +
            "join waiting.ptDate as ptDate " +
            "join ptDate.presentation as presentation " +
            "join presentation.center as center " +
            "where parent.id = :userId")
    Slice<ParticipationListDto> findMyWaiting(@Param("userId") Long userId, Pageable pageable);

    /*
        사용자 id를 파라미터로 받아서 자녀 ID로 부모를 조회합니다.
     */
    @Query("select distinct p " +
            "from Parent p " +
            "left join fetch p.children " +
            "where p.id =:userId")
    Optional<Parent> findByIdWithChild(@Param("userId") Long userId);

    /*
        사용자 id를 파라미터로 받아서 시설에 선호하는 ID로 부모를 조회합니다.
     */
    @Query("select distinct p " +
            "from Parent p " +
            "left join fetch p.prefers pp " +
            "left join fetch pp.center " +
            "where p.id =:userId")
    Optional<Parent> findByIdWithPreferWithCenter(@Param("userId") Long userId);

}
