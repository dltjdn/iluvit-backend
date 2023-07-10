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
        아이들과 함께 있는 부모를 조회합니다.
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
        부모 id로 내 참여를 조회합니다.
     */
    @Query("select parent " +
            "from Parent parent " +
            "where parent.id = :userId")
    Parent findMyParticipation(@Param("userId") Long userId);


    /*
        참여 현황이 참여로 되어있고 부모 id로 참여 리스트 DTO를 조회합니다. (설명회 참여 현황이 참여로 되어있을 때)
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
        참여 현황이 참여로 되어있고 부모 id로 참여 리스트 DTO를 조회합니다. (설명회 참여 현황이 취소로 되어있을 때)
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
        부모 id로 참여 리스트 DTO를 조회합니다. (설명회 참여 현황이 대기로 되어있을 때)
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
        아이 id로 부모를 조회합니다.
     */
    @Query("select distinct p " +
            "from Parent p " +
            "left join fetch p.children " +
            "where p.id =:userId")
    Optional<Parent> findByIdWithChild(@Param("userId") Long userId);


}
