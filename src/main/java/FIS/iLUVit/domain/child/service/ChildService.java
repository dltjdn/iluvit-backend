package FIS.iLUVit.domain.child.service;

import FIS.iLUVit.domain.alarm.service.AlarmService;
import FIS.iLUVit.domain.board.domain.Board;
import FIS.iLUVit.domain.board.repository.BoardRepository;
import FIS.iLUVit.domain.boardbookmark.repository.BoardBookmarkRepository;
import FIS.iLUVit.domain.boardbookmark.service.BoardBookmarkService;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.center.exception.CenterErrorResult;
import FIS.iLUVit.domain.center.exception.CenterException;
import FIS.iLUVit.domain.center.repository.CenterRepository;
import FIS.iLUVit.domain.child.dto.*;
import FIS.iLUVit.domain.child.domain.Child;
import FIS.iLUVit.domain.child.exception.ChildErrorResult;
import FIS.iLUVit.domain.child.exception.ChildException;
import FIS.iLUVit.domain.child.repository.ChildRepository;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.parent.repository.ParentRepository;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.teacher.repository.TeacherRepository;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.repository.UserRepository;
import FIS.iLUVit.domain.center.dto.CenterFindForUserResponse;
import FIS.iLUVit.domain.center.dto.CenterFindForUserRequest;
import FIS.iLUVit.domain.common.domain.Approval;
import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.common.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final AlarmService alarmService;

    /**
     * 아이 정보 전체 조회
     */
    public List<ChildCenterResponse> findAllChild(Long userId) {
        Parent findParent = getParent(userId);

        List<ChildCenterResponse> responses = findParent.getChildren().stream()
                .map(ChildCenterResponse::from)
                .collect(Collectors.toList());

        return responses;
    }


    /**
     * 아이 정보 저장 ( 아이 생성 )
     */
    public  void saveNewChild(Long userId, ChildCreateRequest request) {

        Parent parent = parentRepository.getById(userId);

        // 시설 가져오기
        Center center = getCenterSigned(request.getCenter_id());

        // 아이 등록
        Child newChild = request.createChild(center, parent);

        // 아이 승인 요청 알람이 해당 시설에 승인된 교사들에게 감
        List<Teacher> teachers = teacherRepository.findByCenterAndApproval(center, Approval.ACCEPT);
        alarmService.sendCenterApprovalReceivedAlarm(teachers, Auth.PARENT);

        imageService.saveProfileImage(request.getProfileImg(), newChild);

        childRepository.save(newChild);
    }

    /**
     * 아이 정보 상세 조회
     */
    public ChildFindOneResponse findChildDetails(Long userId, Long childId) {
        User user = getUser(userId);

        Child child = getChildByParent(childId, (Parent) user); // 프로필 수정하고자 하는 아이 가져오기

        return ChildFindOneResponse.from(child);
    }

    /**
     * 아이 정보 수정
     */
    public ChildFindOneResponse updateChildInfo(Long userId, Long childId, ChildUpdateRequest request) {
        User user = getUser(userId);

        Child updatedChild = getChildByParent(childId, (Parent) user); // 수정하고자 하는 아이

        updatedChild.updateChildInfo(request.getName(), request.getBirthDate()); // 프로필 수정

        imageService.saveProfileImage(request.getProfileImg(), updatedChild); // 프로필 이미지 저장

        return ChildFindOneResponse.from(updatedChild);
    }

    /**
     * 아이 삭제
     */
    public List<ChildCenterResponse> deleteChild(Long userId, Long childId) {
        User user = getUser(userId);

        // 삭제하고자 하는 아이
        Child deletedChild = childRepository.findById(childId)
                .orElseThrow(() -> new ChildException(ChildErrorResult.CHILD_NOT_FOUND));

        // 삭제하고자 하는 아이와 같은 시설에 다니는 또 다른 자녀가 있는지 확인해서 없으면 해당 시설과 관련된 bookmark 모두 삭제
        if (deletedChild.getCenter() != null) {
            List<Child> childs = childRepository.findByParent((Parent)user);

            // 아이와 같은 시설에 다니는 또 다른 아이가 없을경우 해당 시설과 관련된 bookmark 모두 삭제
            if (!isAlreadySignedChild(childs, deletedChild)) {
                boardBookmarkService.deleteBoardBookmarkByChild(userId, deletedChild);
            }
        }

        childRepository.delete(deletedChild);
        return findAllChild(userId);
    }


    /**
     * 아이 추가용 시설 정보 조회
     */
    public Slice<CenterFindForUserResponse> findCenterForAddChild(CenterFindForUserRequest request, Pageable pageable) {
        List<CenterFindForUserResponse> centerFindForUserRespons = centerRepository.findCenterForAddChild(request.getSido(), request.getSigungu(), request.getCenterName()).stream()
                .map(CenterFindForUserResponse::from)
                .collect(Collectors.toList());

        boolean hasNext = false;
        if (centerFindForUserRespons.size() > pageable.getPageSize()) {
            hasNext = true;
            centerFindForUserRespons.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(centerFindForUserRespons, pageable, hasNext);

    }

    /**
     * 아이 시설 대기 ( 아이 시설 승인 요청 )
     */
    public Long requestAssignCenterForChild(Long userId, Long childId, Long centerId) {
        User user = getUser(userId);

        // 승인 받고자 하는 아이
        Child mappedChild = childRepository.findByIdAndParent(childId,(Parent)user)
                .orElseThrow(() -> new ChildException(ChildErrorResult.CHILD_NOT_FOUND));

        // 속해있는 시설이 있는 경우
        if (mappedChild.getCenter() != null) {
            throw new UserException(UserErrorResult.ALREADY_BELONGS_TO_CENTER);
        }

        // 시설에 승인 요청
        Center center = getCenterSigned(centerId);
        mappedChild.mappingCenter(center);

        // 승인 요청 알림을 해당 시설의 관리교사에게 전송
        List<Teacher> teachers = teacherRepository.findByCenterAndApproval(center, Approval.ACCEPT);
        alarmService.sendCenterApprovalReceivedAlarm(teachers, Auth.TEACHER);

        return mappedChild.getCenter().getId();
    }


    /**
     * 아이 시설 탈퇴
     */
    public void leaveCenterForChild(Long userId, Long childId) {
        Parent parent = getParent(userId);

        // 요청 사용자가 등록한 모든 아이 가져오기
        List<Child> childrenByUser = childRepository.findByParent(parent);

        // 사용자의 아이중에 childId를 가진 아이가 있는지 검사
        Child exitedChild = childRepository.findByIdAndCenterIsNull(childId)
                .orElseThrow(() -> new ChildException(ChildErrorResult.CHILD_NOT_FOUND));

        // 아이와 같은 시설에 다니는 또 다른 아이가 없을경우 해당 시설과 관련된 bookmark 모두 삭제
        if (!isAlreadySignedChild(childrenByUser, exitedChild)) {
            boardBookmarkService.deleteBoardBookmarkByChild(userId, exitedChild);
        }
        // 시설 탈퇴
        exitedChild.exitCenter();
    }

    /**
     *  아이 승인용 아이 정보 전체 조회
     */
    public List<ChildFindForAdminResponse> findChildApprovalList(Long userId) {
        // 사용자가 속한 시설의 아이들 끌어오기
        Teacher teacher = getTeacherByApproval(userId, Approval.ACCEPT);

        List<ChildFindForAdminResponse> responses = childRepository.findByCenter(teacher.getCenter()).stream()
                .filter(child -> child.getApproval() != Approval.REJECT)
                .map(ChildFindForAdminResponse::from)
                .collect(Collectors.toList());

        return responses;
    }

    /**
     * 아이를 시설에 승인
     */
    public void acceptChildRegistration(Long userId, Long childId) {
        // 사용자가 등록된 시설과 연관된 아이들 목록 가져오기
        Teacher teacher = getTeacherByApproval(userId, Approval.ACCEPT);

        // childId에 해당하는 아이가 시설에 승인 대기중인지 확인
        Child acceptedChild = childRepository.findByIdAndCenterAndApproval(childId, teacher.getCenter(), Approval.WAITING)
                .orElseThrow(() -> new ChildException(ChildErrorResult.CHILD_NOT_FOUND));

        // 승인
        acceptedChild.accepted();

        // 승인하고자 하는 아이의 부모와 그 부모에 속한 모든 아이들 가져오기
        Parent acceptedParent = getParent(acceptedChild.getParent().getId());

        // 승인 완료 알람이 학부모에게로 감
        alarmService.sendCenterApprovalAcceptedAlarm(acceptedParent,teacher.getCenter() );

        // 새로운 시설에 아이가 승인될 경우 해당 시설에 default board 북마크에 추가
        if (!isAlreadySignedChild(acceptedParent.getChildren(), acceptedChild)) {
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
        Teacher teacher = getTeacherByApproval(userId, Approval.ACCEPT);

        Child firedChild = childRepository.findByIdAndCenter(childId, teacher.getCenter())
                .orElseThrow(() -> new ChildException(ChildErrorResult.CHILD_NOT_FOUND));

        // 식제하고자 하는 아이의 부모에 속한 모든 아이들 가져오기
        List<Child> childrenByUser = childRepository.findByParent(firedChild.getParent());

        // 아이와 같은 시설에 다니는 또 다른 아이가 없을경우 해당 시설과 관련된 bookmark 모두 삭제
        if (!isAlreadySignedChild(childrenByUser, firedChild)) {
            boardBookmarkService.deleteBoardBookmarkByChild(firedChild.getParent().getId(), firedChild);
        }
        // 시설과의 연관관계 끊기
        firedChild.exitCenter();
    }


    /**
     * 기존에 있던 아이들중에 현재 아이와 같은 시설에 다니는 또 다른 아이가 있는지 검사
     */
    private boolean isAlreadySignedChild(List<Child> childrenByUser, Child findChild) {
        Optional<Child> sameCenterChildren = childrenByUser.stream()
                .filter(child-> child.getCenter() != null)
                .filter(child -> Objects.equals(child.getCenter().getId(), findChild.getCenter().getId()))
                .filter(child -> !Objects.equals(child.getId(), findChild.getId()))
                .filter(child -> child.getApproval() == Approval.ACCEPT)
                .findFirst();

        if(sameCenterChildren.isEmpty()) return false;
        else return true;
    }


    /**
     * 아이 삭제 & 아이가 연관된 유치원 연관관계 끊기(해당 시설과 관련된 bookmark 모두 삭제)
     */
    public void deleteChildByWithdraw(Long userId, Parent parent){
        childRepository.findByParent(parent).forEach(child -> {
            deleteChild(userId, child.getId());
        });
    }

    /**
     * 예외처리 - 존재하는 유저인가
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 해당 학부모의 아이인가
     */
    private Child getChildByParent(Long childId, Parent user) {
        return childRepository.findByIdAndParent(childId, user)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 승인여부에 해당하는 선생인가
     */
    private Teacher getTeacherByApproval(Long userId, Approval approval) {
        return teacherRepository.findByIdAndApproval(userId, approval)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 학부모인가
     */
    private Parent getParent(Long userId) {
        return parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 signed 된 시설인가
     */
    private Center getCenterSigned(Long centerId) {
        return centerRepository.findByIdAndSigned(centerId, true)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));
    }


}
