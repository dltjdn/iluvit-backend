package FIS.iLUVit.repository;

import FIS.iLUVit.controller.dto.ParticipationListDto;
import FIS.iLUVit.domain.Parent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    @Query("select distinct p " +
            "from Parent p " +
            "left join fetch p.children c " +
            "left join fetch c.center " +
            "where p.id = :id")
    Optional<Parent> findWithChildren(@Param("id") Long id);

    Optional<Parent> findByNickName(String nickname);

    @Query("select parent " +
            "from Parent parent " +
            "left join fetch parent.participations as participation " +
            "left join fetch participation.ptDate as ptDate " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch presentation.center as center " +
            "where parent.id = :userId")
    Parent findMyParticipation(@Param("userId") Long userId);

    @Query("select parent " +
            "from Parent parent " +
            "left join fetch parent.waitings as waiting " +
            "left join fetch waiting.ptDate as ptDate " +
            "left join fetch ptDate.presentation as presentation " +
            "left join fetch presentation.center as center " +
            "where parent.id = :userId")
    Parent findMyWaiting(@Param("userId") Long userId);

    @Query("select new FIS.iLUVit.controller.dto.ParticipationListDto(" +
            "parent.id, participation.id, ptDate.id, presentation.id, center.id, ptDate.date, ptDate.time, center.profileImagePath, presentation.place, presentation.content, center.name, center.tel, center.address, center.addressDetail, ptDate.ablePersonNum, ptDate.participantCnt, participation.status" +
            ") " +
            "from Parent parent " +
            "join parent.participations as participation " +
            "join participation.ptDate as ptDate " +
            "join ptDate.presentation as presentation " +
            "join presentation.center as center " +
            "where parent.id = :userId and participation.status = FIS.iLUVit.domain.enumtype.Status.JOINED")
    Slice<ParticipationListDto> findMyJoinParticipation(@Param("userId") Long userId, Pageable pageable);

    @Query("select new FIS.iLUVit.controller.dto.ParticipationListDto(" +
            "parent.id, participation.id, ptDate.id, presentation.id, center.id, ptDate.date, ptDate.time, center.profileImagePath, presentation.place, presentation.content, center.name, center.tel, center.address, center.addressDetail, ptDate.ablePersonNum, ptDate.participantCnt, participation.status" +
            ") " +
            "from Parent parent " +
            "join parent.participations as participation " +
            "join participation.ptDate as ptDate " +
            "join ptDate.presentation as presentation " +
            "join presentation.center as center " +
            "where parent.id = :userId and participation.status = FIS.iLUVit.domain.enumtype.Status.CANCELED")
    Slice<ParticipationListDto> findMyCancelParticipation(@Param("userId") Long userId, Pageable pageable);

    @Query("select new FIS.iLUVit.controller.dto.ParticipationListDto(" +
            "parent.id, waiting.id, ptDate.id, presentation.id, center.id, ptDate.date, ptDate.time, center.profileImagePath, presentation.place, presentation.content, center.name, center.tel, center.address, center.addressDetail, ptDate.ablePersonNum, ptDate.participantCnt" +
            ") " +
            "from Parent parent " +
            "join parent.waitings as waiting " +
            "join waiting.ptDate as ptDate " +
            "join ptDate.presentation as presentation " +
            "join presentation.center as center " +
            "where parent.id = :userId")
    Slice<ParticipationListDto> findMyWaiting(@Param("userId") Long userId, Pageable pageable);

    @Query("select distinct p " +
            "from Parent p " +
            "left join fetch p.children " +
            "where p.id =:userId")
    Optional<Parent> findByIdWithChild(@Param("userId") Long userId);

    @Query("select distinct p " +
            "from Parent p " +
            "left join fetch p.prefers pp " +
            "left join fetch pp.center " +
            "where p.id =:userId")
    Optional<Parent> findByIdWithPreferWithCenter(@Param("userId") Long userId);

}
