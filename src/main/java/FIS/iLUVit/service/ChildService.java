package FIS.iLUVit.service;

import FIS.iLUVit.domain.iluvit.Alarm;
import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.Board;
import FIS.iLUVit.domain.iluvit.Child;
import FIS.iLUVit.domain.iluvit.Parent;
import FIS.iLUVit.domain.iluvit.Teacher;
import FIS.iLUVit.dto.center.CenterDto;
import FIS.iLUVit.dto.center.CenterRequest;
import FIS.iLUVit.dto.child.*;
import FIS.iLUVit.domain.iluvit.CenterApprovalAcceptedAlarm;
import FIS.iLUVit.domain.iluvit.CenterApprovalReceivedAlarm;
import FIS.iLUVit.domain.iluvit.enumtype.Approval;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.domain.iluvit.enumtype.Auth;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.common.CenterRepository;
import FIS.iLUVit.repository.iluvit.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChildService {

    private final ImageService imageService;
    private final BoardBookmarkService boardBookmarkService;
    private final ParentRepository parentRepository;
    private final CenterRepository centerRepository;
    private final ChildRepository childRepository;
    private final BoardRepository boardRepository;
    private final BoardBookmarkRepository boardBookmarkRepository;
    private final TeacherRepository teacherRepository;
    private final ChildRepository childrenRepository;

    private final AlarmRepository alarmRepository;

    /**
     * 작성자: 이승범
     * 작성내용: 부모의 메인페이지에 필요한 아이들 정보 반환
     */
    public List<ChildDto> childInfo(Long id) {
        Parent findParent = parentRepository.findWithChildren(id)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        List<ChildDto> childDtos = new ArrayList<>();

        findParent.getChildren().forEach(child -> {
            childDtos.add(
                    new ChildDto(child, imageService.getProfileImage(child))
            );
        });

        return childDtos;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 추가
     */
    public Child saveChild(Long userId, ChildDetailRequest request) throws IOException {

        Parent parent = parentRepository.getById(userId);

        // 새로 등록할 시설에 교사들 엮어서 가져오기
        Center center = centerRepository.findByIdAndSignedWithTeacher(request.getCenter_id())
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 아이 등록
        Child newChild = request.createChild(center, parent);

        // 아이 승인 요청 알람이 해당 시설에 승인된 교사들에게 감
        teacherRepository.findByCenterId(center.getId()).forEach(teacher -> {
            Alarm alarm = new CenterApprovalReceivedAlarm(teacher, Auth.PARENT, teacher.getCenter());
            alarmRepository.save(alarm);
            AlarmUtils.publishAlarmEvent(alarm);

        });

        imageService.saveProfileImage(request.getProfileImg(), newChild);

        childRepository.save(newChild);

        return newChild;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 프로필 조회
     */
    public ChildDetailResponse findChildInfoDetail(Long userId, Long childId) {
        // 프로필 수정하고자 하는 아이 가져오기
        Child child = childRepository.findByIdAndParentWithCenter(userId, childId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        ChildDetailResponse response = new ChildDetailResponse(child,imageService.getProfileImage(child));

        return response;
    }

    /**
    *   작성자: 이승범
    *   작성내용: 아이 프로필 수정
    */
    public ChildDetailResponse updateChild(Long userId, Long childId, ChildRequest request) {
        // 수정하고자 하는 아이
        Child updatedChild = childRepository.findByIdAndParentWithCenter(userId, childId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 프로필 수정
        updatedChild.update(request.getName(), request.getBirthDate());

        ChildDetailResponse response = new ChildDetailResponse(updatedChild, imageService.getProfileImage(updatedChild));

        // 프로필 이미지 수정
        imageService.saveProfileImage(request.getProfileImg(), updatedChild);

        return response;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 학부모/아이 시설 승인 요청
     */
    public Child mappingCenter(Long userId, Long childId, Long centerId) {

        // 승인 받고자 하는 아이
        Child mappedChild = childRepository.findByIdAndParent(userId, childId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 속해있는 시설이 있는 경우
        if (mappedChild.getCenter() != null) {
            throw new SignupException(SignupErrorResult.ALREADY_BELONG_CENTER);
        }

        // 승인 요청 보내는 시설
        Center center = centerRepository.findByIdAndSignedWithTeacher(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        mappedChild.mappingCenter(center);
        teacherRepository.findByCenterId(center.getId()).forEach(teacher -> {
            Alarm alarm = new CenterApprovalReceivedAlarm(teacher, Auth.PARENT, teacher.getCenter());
            alarmRepository.save(alarm);
            AlarmUtils.publishAlarmEvent(alarm);
        });

        return mappedChild;
    }

    /**
    *   작성자: 이승범
    *   작성내용: 아이의 시설 탈퇴
    */
    public Child exitCenter(Long userId, Long childId) {
        // 요청 사용자가 등록한 모든 아이 가져오기
        List<Child> childrenByUser = childRepository.findByUserWithCenter(userId);

        // 사용자의 아이중에 childId를 가진 아이가 있는지 검사
        Child exitedChild = childrenByUser.stream()
                .filter(child -> Objects.equals(child.getId(), childId))
                .filter(child -> child.getCenter() != null)
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        deleteBookmarkByCenter(userId, childrenByUser, exitedChild);

        exitedChild.exitCenter();

        return exitedChild;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 삭제
     */
    public List<ChildDto> deleteChild(Long userId, Long childId) {

        // 요청 사용자가 등록한 모든 아이 가져오기
        List<Child> childrenByUser = childRepository.findByUserWithCenter(userId);

        // 삭제하고자 하는 아이
        Child deletedChild = childrenByUser.stream()
                .filter(child -> Objects.equals(child.getId(), childId))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 삭제하고자 하는 아이와 같은 시설에 다니는 또 다른 자녀가 있는지 확인해서 없으면 해당 시설과 관련된 bookmark 모두 삭제
        if (deletedChild.getCenter() != null) {
            deleteBookmarkByCenter(userId, childrenByUser, deletedChild);
        }

        childRepository.delete(deletedChild);
        return childInfo(userId);
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 승인 페이지를 위한 시설에 등록된 아이들 정보 조회
     */
    public List<ChildInfoForAdminDto> findChildApprovalInfoList(Long userId) {
        // 사용자가 속한 시설의 아이들 끌어오기
        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        List<ChildInfoForAdminDto> response = new ArrayList<>();


        childrenRepository.findByCenter(teacher.getCenter()).forEach(child -> {
            // 해당시설에 대해 거절/삭제 당하지 않은 아이들만 보여주기
            if (child.getApproval() != Approval.REJECT) {
                ChildInfoForAdminDto childInfo =
                        new ChildInfoForAdminDto(child, imageService.getProfileImage(child));

                response.add(childInfo);
            }
        });

        return response;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 아이 승인
     */
    public Child acceptChild(Long userId, Long childId) {

        // 사용자가 등록된 시설과 연관된 아이들 목록 가져오기
        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        // childId에 해당하는 아이가 시설에 승인 대기중인지 확인
        Child acceptedChild = childrenRepository.findByCenter(teacher.getCenter()).stream()
                .filter(child -> Objects.equals(child.getId(), childId) && child.getApproval() == Approval.WAITING)
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 승인
        acceptedChild.accepted();

        // 승인하고자 하는 아이의 부모와 그 부모에 속한 모든 아이들 가져오기
        Parent acceptedParent = parentRepository.findByIdWithChild(acceptedChild.getParent().getId())
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 승인 완료 알람이 학부모에게로 감
        Alarm alarm = new CenterApprovalAcceptedAlarm(acceptedParent, teacher.getCenter());
        alarmRepository.save(alarm);
        AlarmUtils.publishAlarmEvent(alarm);
        // bookmark 처리
        // 기존에 있던 아이들중에 현재 승인되는 아이와 같은 시설에 다니는 또 다른 아이가 있는지 검사
        Optional<Child> alreadySignedChild = acceptedParent.getChildren().stream()
                .filter(child -> child.getCenter() != null)
                .filter(child -> Objects.equals(child.getCenter().getId(), teacher.getCenter().getId()))
                .filter(child -> child.getApproval() == Approval.ACCEPT)
                .filter(child -> !Objects.equals(child.getId(), acceptedChild.getId()))
                .findFirst();

        // 새로운 시설에 아이가 승인될 경우 해당 시설에 default board 북마크에 추가
        if (alreadySignedChild.isEmpty()) {
            // 승인하고자 하는 시설의 게시판들 lazyLoading 통해 가져오기
            Long centerId = teacher.getCenter().getId();
            boardRepository.findByCenter(centerId).stream().map(board-> {
                if (board.getIsDefault()) {
                    boardBookmarkService.create(acceptedParent.getId(), board.getId());
                }
                return null;
            });


        }

        return acceptedChild;
    }

    /**
     * 작성자: 이승범
     * 작성내용: 시설에서 아이 삭제/승인거절
     */
    public void fireChild(Long userId, Long childId) {

        // 사용자가 등록된 시설과 연관된 아이들 목록 가져오기
        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        // childId 검증
        Child firedChild = childrenRepository.findByCenter(teacher.getCenter()).stream()
                .filter(child -> Objects.equals(child.getId(), childId))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));


        // 식제하고자 하는 아이의 부모와 그 부모에 속한 모든 아이들 가져오기
        List<Child> childrenByUser = childRepository.findByUserWithCenter(userId);

        // bookmark 처리
        deleteBookmarkByCenter(firedChild.getParent().getId(), childrenByUser, firedChild);

        // 시설과의 연관관계 끊기
        firedChild.exitCenter();
    }

    // 삭제되는 아이와 같은 시설에 다니는 또 다른 아이가 없을경우 해당 시설과 관련된 bookmark 모두 삭제
    public void deleteBookmarkByCenter(Long parentId, List<Child> childrenByUser, Child deletedChild) {
        Optional<Child> sameCenterChildren = childrenByUser.stream()
                .filter(child-> child.getCenter() != null)
                .filter(child -> Objects.equals(child.getCenter().getId(), deletedChild.getCenter().getId()))
                .filter(child -> !Objects.equals(child.getId(), deletedChild.getId()))
                .filter(child -> child.getApproval() == Approval.ACCEPT)
                .findFirst();

        // 없으면 해당 시설과 연관된 bookmark 싹 다 삭제
        if (sameCenterChildren.isEmpty()) {
            List<Board> boards = boardRepository.findByCenter(deletedChild.getCenter().getId());
            List<Long> boardIds = boards.stream()
                    .map(Board::getId)
                    .collect(Collectors.toList());
            boardBookmarkRepository.deleteAllByBoardAndUser(parentId, boardIds);
        }
    }

    /**
     *   작성자: 이승범
     *   작성내용: 아이추가 과정에서 필요한 센터정보 가져오기
     */
    public Slice<CenterDto> findCenterForAddChild(CenterRequest request, Pageable pageable) {
        return centerRepository.findCenterForAddChild(request.getSido(), request.getSigungu(), request.getCenterName(), pageable);
    }

}
