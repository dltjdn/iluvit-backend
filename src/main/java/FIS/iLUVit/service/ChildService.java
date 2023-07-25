package FIS.iLUVit.service;

import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.dto.center.CenterDto;
import FIS.iLUVit.dto.center.CenterRequest;
import FIS.iLUVit.dto.child.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.CenterApprovalAcceptedAlarm;
import FIS.iLUVit.domain.alarms.CenterApprovalReceivedAlarm;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    /**
     * 아이 정보 전체 조회
     */
    public List<ChildDto> findChildList(Long userId) {
        Parent findParent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        List<ChildDto> childDtos = new ArrayList<>();

        findParent.getChildren().forEach(child -> {
            childDtos.add(
                    new ChildDto(child, child.getProfileImagePath())
            );
        });

        return childDtos;
    }

    /**
     * 아이 정보 저장 ( 아이 생성 )
     */
    public  void saveNewChild(Long userId, ChildDetailRequest request) {

        Parent parent = parentRepository.getById(userId);

        // 시설 가져오기
        Center center = centerRepository.findByIdAndSigned(request.getCenterId(), true)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 아이 등록
        Child newChild = request.createChild(center, parent);

        // 아이 승인 요청 알람이 해당 시설에 승인된 교사들에게 감
        List<Teacher> teacherList = teacherRepository.findByCenterWithApproval(center.getId());
        teacherList.forEach(teacher -> {
            Alarm alarm = new CenterApprovalReceivedAlarm(teacher, Auth.PARENT, teacher.getCenter());
            alarmRepository.save(alarm);
            AlarmUtils.publishAlarmEvent(alarm);

        });

        imageService.saveProfileImage(request.getProfileImg(), newChild);

        childRepository.save(newChild);
    }

    /**
     * 아이 정보 상세 조회
     */
    public ChildDetailResponse findChildDetails(Long userId, Long childId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 프로필 수정하고자 하는 아이 가져오기
        Child child = childRepository.findByIdAndParent(childId, (Parent)user)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        ChildDetailResponse childDetailResponse = new ChildDetailResponse(child,child.getProfileImagePath());

        return childDetailResponse;
    }

    /**
     * 아이 정보 수정
     */
    public void modifyChildInfo(Long userId, Long childId, ChildRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 수정하고자 하는 아이
        Child updatedChild = childRepository.findByIdAndParent(childId,(Parent)user)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 프로필 수정
        updatedChild.update(request.getName(), request.getBirthDate());

        ChildDetailResponse childDetailResponse = new ChildDetailResponse(updatedChild, updatedChild.getProfileImagePath());

        // 프로필 이미지 수정
        imageService.saveProfileImage(request.getProfileImg(), updatedChild);

    }

    /**
     * 아이 삭제
     */
    public void deleteChild(Long userId, Long childId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 요청 사용자가 등록한 모든 아이 가져오기
        List<Child> childrenByUser = childRepository.findByParent((Parent)user);

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
    }


    /**
     * 아이 추가용 시설 정보 조회
     */
    public Slice<CenterDto> findCenterForAddChild(CenterRequest request, Pageable pageable) {
        List<CenterDto> centerDtos = centerRepository.findCenterForAddChild(request.getSido(), request.getSigungu(), request.getCenterName()).stream()
                .map(CenterDto::new)
                .collect(Collectors.toList());

        boolean hasNext = false;
        if (centerDtos.size() > pageable.getPageSize()) {
            hasNext = true;
            centerDtos.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(centerDtos, pageable, hasNext);

    }

    /**
     * 아이 시설 대기 ( 아이 시설 승인 요청 )
     */
    public void requestAssignCenterForChild(Long userId, Long childId, Long centerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 승인 받고자 하는 아이
        Child mappedChild = childRepository.findByIdAndParent(childId,(Parent)user)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 속해있는 시설이 있는 경우
        if (mappedChild.getCenter() != null) {
            throw new SignupException(SignupErrorResult.ALREADY_BELONG_CENTER);
        }

        // 승인 요청 보내는 시설
        Center center = centerRepository.findByIdAndSigned(centerId, true)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        mappedChild.mappingCenter(center);

        List<Teacher> teacherList = teacherRepository.findByCenterWithApproval(centerId);

        teacherList.forEach(teacher -> {
            Alarm alarm = new CenterApprovalReceivedAlarm(teacher, Auth.PARENT, teacher.getCenter());
            alarmRepository.save(alarm);
            AlarmUtils.publishAlarmEvent(alarm);
        });
    }

    /**
     * 아이 시설 탈퇴
     */
    public void leaveCenterForChild(Long userId, Long childId) {
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 요청 사용자가 등록한 모든 아이 가져오기
        List<Child> childrenByUser = childRepository.findByParent(parent);

        // 사용자의 아이중에 childId를 가진 아이가 있는지 검사
        Child exitedChild = childrenByUser.stream()
                .filter(child -> Objects.equals(child.getId(), childId))
                .filter(child -> child.getCenter() != null)
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        deleteBookmarkByCenter(userId, childrenByUser, exitedChild);

        exitedChild.exitCenter();
    }

    /**
     *  아이 승인용 아이 정보 전체 조회
     */
    public List<ChildInfoForAdminDto> findChildApprovalList(Long userId) {
        // 사용자가 속한 시설의 아이들 끌어오기
        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        List<ChildInfoForAdminDto> childInfoForAdminDtos = new ArrayList<>();

        List<Child> childList = childRepository.findByCenter(teacher.getCenter());
        childList.forEach(child -> {
            // 해당시설에 대해 거절/삭제 당하지 않은 아이들만 보여주기
            if (child.getApproval() != Approval.REJECT) {
                ChildInfoForAdminDto childInfo =
                        new ChildInfoForAdminDto(child, child.getProfileImagePath());

                childInfoForAdminDtos.add(childInfo);
            }
        });
        return childInfoForAdminDtos;
    }

    /**
     * 아이를 시설에 승인
     */
    public void acceptChildRegistration(Long userId, Long childId) {

        // 사용자가 등록된 시설과 연관된 아이들 목록 가져오기
        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));
        // childId에 해당하는 아이가 시설에 승인 대기중인지 확인
        List<Child> childList = childRepository.findByCenter(teacher.getCenter());
        Child acceptedChild = childList.stream()
                .filter(child -> Objects.equals(child.getId(), childId) && child.getApproval() == Approval.WAITING)
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 승인
        acceptedChild.accepted();

        // 승인하고자 하는 아이의 부모와 그 부모에 속한 모든 아이들 가져오기
        Parent acceptedParent = parentRepository.findById(acceptedChild.getParent().getId())
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
            // 선생이 가입되어 있는 시설의 게시판 가져오기
            List<Board> boardList = boardRepository.findByCenter(teacher.getCenter());
            // 게시판이 default면 게시판 즐겨찾기에 등록
            boardList.forEach(board -> {
                if (board.getIsDefault()) {
                    boardBookmarkService.saveBoardBookmark(acceptedParent.getId(), board.getId());
                }
            });
        }
    }

    /**
     * 시설에서 아이 삭제/승인거절
     */
    public void rejectChildRegistration(Long userId, Long childId) {

        // 사용자가 등록된 시설과 연관된 아이들 목록 가져오기
        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        // childId 검증
        List<Child> childList = childRepository.findByCenter(teacher.getCenter());
        Child firedChild = childList.stream()
                .filter(child -> Objects.equals(child.getId(), childId))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));


        // 식제하고자 하는 아이의 부모에 속한 모든 아이들 가져오기
        List<Child> childrenByUser = childRepository.findByParent(firedChild.getParent());

        // bookmark 처리
        deleteBookmarkByCenter(firedChild.getParent().getId(), childrenByUser, firedChild);

        // 시설과의 연관관계 끊기
        firedChild.exitCenter();
    }

    /**
     *  삭제되는 아이와 같은 시설에 다니는 또 다른 아이가 없을경우 해당 시설과 관련된 bookmark 모두 삭제
     */
    public void deleteBookmarkByCenter(Long parentId, List<Child> childrenByUser, Child deletedChild) {
        Optional<Child> sameCenterChildren = childrenByUser.stream()
                .filter(child-> child.getCenter() != null)
                .filter(child -> Objects.equals(child.getCenter().getId(), deletedChild.getCenter().getId()))
                .filter(child -> !Objects.equals(child.getId(), deletedChild.getId()))
                .filter(child -> child.getApproval() == Approval.ACCEPT)
                .findFirst();

        // 없으면 해당 시설과 연관된 bookmark 싹 다 삭제
        if (sameCenterChildren.isEmpty()) {
            List<Board> boards = boardRepository.findByCenter(deletedChild.getCenter());
            User user = userRepository.findById(parentId)
                    .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
            boardBookmarkRepository.deleteByUserAndBoardIn(user, boards);
        }
    }
}
