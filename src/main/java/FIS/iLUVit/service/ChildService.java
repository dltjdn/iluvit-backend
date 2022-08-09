package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.CenterApprovalAcceptedAlarm;
import FIS.iLUVit.domain.alarms.CenterApprovalReceivedAlarm;
import FIS.iLUVit.domain.enumtype.Approval;
<<<<<<< HEAD
import FIS.iLUVit.exception.*;
=======
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
>>>>>>> e25daceb30bab15bcb8052179b30adffc2555674
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private final BookmarkService bookmarkService;
    private final ParentRepository parentRepository;
    private final CenterRepository centerRepository;
    private final ChildRepository childRepository;
    private final BoardRepository boardRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TeacherRepository teacherRepository;

    /**
     * 작성날짜: 2022/05/13 4:43 PM
     * 작성자: 이승범
     * 작성내용: 부모의 메인페이지에 필요한 아이들 정보 반환
     */
    public ChildInfoDTO childrenInfo(Long id) {
        Parent findParent = parentRepository.findWithChildren(id)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_TOKEN));

        ChildInfoDTO childInfoDTO = new ChildInfoDTO();

        findParent.getChildren().forEach(child -> {
            childInfoDTO.getData().add(
                    new ChildInfoDTO.ChildInfo(child, imageService.getProfileImage(child))
            );
        });

        return childInfoDTO;
    }

    /**
     * 작성날짜: 2022/06/23 5:25 PM
     * 작성자: 이승범
     * 작성내용: 아이 추가
     */
    public Child saveChild(Long userId, SaveChildRequest request) throws IOException {

        Parent parent = parentRepository.getById(userId);

        // 새로 등록할 시설에 교사들 엮어서 가져오기
        Center center = centerRepository.findByIdAndSignedWithTeacher(request.getCenter_id())
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 아이 등록
        Child newChild = request.createChild(center, parent);
        childRepository.save(newChild);

        // 아이 승인 요청 알람이 해당 시설에 승인된 교사들에게 감
        center.getTeachers().forEach(teacher -> {
            AlarmUtils.publishAlarmEvent(new CenterApprovalReceivedAlarm(teacher, Auth.PARENT));
        });

        imageService.saveProfileImage(request.getProfileImg(), newChild);

        return newChild;
    }

    /**
     * 작성날짜: 2022/06/27 4:57 PM
     * 작성자: 이승범
     * 작성내용: 아이 프로필 조회
     */
    public ChildInfoDetailResponse findChildInfoDetail(Long userId, Long childId) {
        // 프로필 수정하고자 하는 아이 가져오기
        Child child = childRepository.findByIdAndParentWithCenter(userId, childId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        ChildInfoDetailResponse response = new ChildInfoDetailResponse(child);

        response.setProfileImage(imageService.getProfileImage(child));

        return response;
    }

    /**
    *   작성날짜: 2022/08/08 3:41 PM
    *   작성자: 이승범
    *   작성내용: 아이 프로필 수정
    */
    public ChildInfoDetailResponse updateChild(Long userId, Long childId, UpdateChildRequest request) {
        // 수정하고자 하는 아이
        Child updatedChild = childRepository.findByIdAndParentWithCenter(userId, childId)
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 프로필 수정
        updatedChild.update(request.getName(), request.getBirthDate());

        ChildInfoDetailResponse response = new ChildInfoDetailResponse(updatedChild);

        // 프로필 이미지 수정
        imageService.saveProfileImage(request.getProfileImg(), updatedChild);
        response.setProfileImage(imageService.getProfileImage(updatedChild));

        return response;
    }

    /**
     * 작성날짜: 2022-08-09 오후 6:01
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

        center.getTeachers().forEach(teacher -> {
            AlarmUtils.publishAlarmEvent(new CenterApprovalReceivedAlarm(teacher, Auth.PARENT));
        });

        return mappedChild;
    }

    /**
    *   작성날짜: 2022/08/08 3:58 PM
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
     * 작성날짜: 2022/06/28 3:18 PM
     * 작성자: 이승범
     * 작성내용: 아이 삭제
     */
    public ChildInfoDTO deleteChild(Long userId, Long childId) {

        // 요청 사용자가 등록한 모든 아이 가져오기
        List<Child> childrenByUser = childRepository.findByUserWithCenter(userId);

        // 삭제하고자 하는 아이
        Child deletedChild = childrenByUser.stream()
                .filter(child -> Objects.equals(child.getId(), childId))
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 삭제하고자 하는 아이와 같은 시설에 다니는 또 다른 자녀가 있는지 확인해서 없으면 해당 시설과 관련된 bookmark 모두 삭제
        deleteBookmarkByCenter(userId, childrenByUser, deletedChild);

        childRepository.delete(deletedChild);
        return childrenInfo(userId);
    }

    /**
     * 작성날짜: 2022/06/30 10:36 AM
     * 작성자: 이승범
     * 작성내용: 아이 승인 페이지를 위한 시설에 등록된 아이들 정보 조회
     */
    public ChildApprovalListResponse findChildApprovalInfoList(Long userId) {
        // 사용자가 속한 시설의 아이들 끌어오기
        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        ChildApprovalListResponse response = new ChildApprovalListResponse();

        teacher.getCenter().getChildren().forEach(child -> {
            // 해당시설에 대해 거절/삭제 당하지 않은 아이들만 보여주기
            if (child.getApproval() != Approval.REJECT) {

                ChildApprovalListResponse.ChildInfoForAdmin childInfo =
                        new ChildApprovalListResponse.ChildInfoForAdmin(child);
                childInfo.setChild_profileImg(imageService.getProfileImage(child));

                response.getData().add(childInfo);
            }
        });
        return response;
    }

    /**
     * 작성날짜: 2022/06/30 3:13 PM
     * 작성자: 이승범
     * 작성내용: 아이 승인
     */
    public Child acceptChild(Long userId, Long childId) {

        // 사용자가 등록된 시설과 연관된 아이들 목록 가져오기
        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        // childId에 해당하는 아이가 시설에 승인 대기중인지 확인
        Child acceptedChild = teacher.getCenter().getChildren().stream()
                .filter(child -> Objects.equals(child.getId(), childId) && child.getApproval() == Approval.WAITING)
                .findFirst()
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 승인
        acceptedChild.accepted();

        // 승인하고자 하는 아이의 부모와 그 부모에 속한 모든 아이들 가져오기
        Parent acceptedParent = parentRepository.findByIdWithChild(acceptedChild.getParent().getId())
                .orElseThrow(() -> new UserException(UserErrorResult.NOT_VALID_REQUEST));

        // 승인 완료 알람이 학부모에게로 감
        AlarmUtils.publishAlarmEvent(new CenterApprovalAcceptedAlarm(acceptedParent, teacher.getCenter()));

        // bookmark 처리
        // 기존에 있던 아이들중에 현재 승인되는 아이와 같은 시설에 다니는 또 다른 아이가 있는지 검사
        Optional<Child> alreadySignedChild = acceptedParent.getChildren().stream()
                .filter(child -> Objects.equals(child.getCenter().getId(), teacher.getCenter().getId()))
                .filter(child -> child.getApproval() == Approval.ACCEPT)
                .filter(child -> !Objects.equals(child.getId(), acceptedChild.getId()))
                .findFirst();

        // 새로운 시설에 아이가 승인될 경우 해당 시설에 default board 북마크에 추가
        if (alreadySignedChild.isEmpty()) {
            // 승인하고자 하는 시설의 게시판들 lazyLoading 통해 가져오기
            teacher.getCenter().getBoards().forEach(board -> {
                if (board.getIsDefault()) {
                    bookmarkService.create(acceptedParent.getId(), board.getId());
                }
            });
        }

        return acceptedChild;
    }

    /**
     * 작성날짜: 2022/06/30 4:28 PM
     * 작성자: 이승범
     * 작성내용: 시설에서 아이 삭제/승인거절
     */
    public void fireChild(Long userId, Long childId) {

        // 사용자가 등록된 시설과 연관된 아이들 목록 가져오기
        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.HAVE_NOT_AUTHORIZATION));

        // childId 검증
        Child firedChild = teacher.getCenter().getChildren().stream()
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
                .filter(child -> child.getCenter() != null)
                .filter(child -> Objects.equals(child.getCenter().getId(), deletedChild.getCenter().getId()))
                .filter(child -> !Objects.equals(child.getId(), deletedChild.getId()))
                .filter(child -> child.getApproval() == Approval.ACCEPT)
                .findFirst();

        // 없으면 해당 시설과 연관된 bookmark 싹 다 삭제
        if (sameCenterChildren.isEmpty()) {
            bookmarkRepository.deleteAllByCenterAndUser(parentId, deletedChild.getCenter().getId());
        }
    }

}
