package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.CenterApprovalAcceptedAlarm;
import FIS.iLUVit.domain.alarms.CenterApprovalReceivedAlarm;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.exception.UserException;
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
                .orElse(null);

        ChildInfoDTO childInfoDTO = new ChildInfoDTO();

        if (findParent != null) {
            findParent.getChildren().forEach(child -> {
                String imagePath = imageService.getChildProfileDir();
                String encodedImage = imageService.getEncodedProfileImage(imagePath, child.getId());
                childInfoDTO.getData().add(new ChildInfoDTO.ChildInfo(child, encodedImage));
            });
        }

        return childInfoDTO;
    }

    /**
     * 작성날짜: 2022/06/23 5:25 PM
     * 작성자: 이승범
     * 작성내용: 아이 추가
     */
    public void saveChild(Long userId, SaveChildRequest request) throws IOException {

        // 부모를 기존에 등록되어 있는 아이와 엮어서 가져오기
        Parent parent = parentRepository.findByIdWithChild(userId);

        // 새로 등록할 시설에 정보를 게시판 정보와 엮어서 가져오기
        Center center = centerRepository.findByIdAndSignedWithTeacher(request.getCenter_id())
                .orElseThrow(() -> new CenterException("잘못된 centerId 입니다."));

        // 아이 등록
        Child newChild = request.createChild(center, parent);
        childRepository.save(newChild);

        // 아이 승인 요청 알람이 해당 시설의 모든 교사에게 감
        center.getTeachers().forEach(teacher -> {
            AlarmUtils.publishAlarmEvent(new CenterApprovalReceivedAlarm(teacher));
        });

        // 프로필 이미지 설정
        if (!request.getProfileImg().isEmpty()) {
            String imagePath = imageService.getChildProfileDir();
            imageService.saveProfileImage(request.getProfileImg(), imagePath + newChild.getId());
        }
    }

    /**
    *   작성날짜: 2022/06/27 4:57 PM
    *   작성자: 이승범
    *   작성내용: 아이 프로필 조회
    */
    public ChildInfoDetailResponse findChildInfoDetail(Long userId, Long childId, Pageable pageable) {
        // 프로필 수정하고자 하는 아이 가져오기
        Child child = childRepository.findByIdWithParentAndCenter(userId, childId)
                        .orElseThrow(() -> new UserException("잘못된 child_id 입니다."));

        ChildInfoDetailResponse response = new ChildInfoDetailResponse(child);

        // 이미지가 있다면 가져오기
        if (child.getHasProfileImg() != null && child.getHasProfileImg()) {
            String imagePath = imageService.getChildProfileDir();
            String image = imageService.getEncodedProfileImage(imagePath, child.getId());
            response.setProfileImage(image);
        }

        // 프로필 수정에 필요한 시설정보들 가져오기
        Slice<CenterInfoDto> centerInfos = centerRepository.findCenterForAddChild(child.getCenter().getArea().getSido(),
                child.getCenter().getArea().getSigungu(), child.getCenter().getName(), pageable);
        response.setCenterInfoDtoSlice(centerInfos);

        return response;
    }

    /**
    *   작성날짜: 2022/06/27 5:47 PM
    *   작성자: 이승범
    *   작성내용: 아이 프로필 수정
    */
    public ChildInfoDetailResponse updateChild(Long userId, Long childId, UpdateChildRequest request, Pageable pageable) throws IOException {

        // 요청 사용자가 등록한 모든 아이 가져오기
        List<Child> childrenByUser = childRepository.findByUserWithCenter(userId);

        Child updatedChild = childrenByUser.stream()
                .filter(child -> Objects.equals(child.getId(), childId))
                .findFirst()
                .orElseThrow(() -> new UserException("잘못된 childId 입니다."));

        Center center = centerRepository.findByIdAndSignedWithTeacher(request.getCenter_id())
                .orElseThrow(() -> new CenterException("올바르지 않은 centerId 입니다."));

        // 시설을 변경하는 경우 bookmark 처리
        if (!Objects.equals(center.getId(), request.getCenter_id())) {
            deleteBookmarkByCenter(userId, childrenByUser, updatedChild);
            // 아이 승인 요청 알람이 해당 시설의 모든 교사에게 감
            center.getTeachers().forEach(teacher -> {
                AlarmUtils.publishAlarmEvent(new CenterApprovalReceivedAlarm(teacher));
            });
        }

        // update 진행
        updatedChild.update(center, request.getName(), request.getBirthDate(), request.getProfileImg());

        ChildInfoDetailResponse response = new ChildInfoDetailResponse(updatedChild);

        // 이미지가 있는 경우
        if (!request.getProfileImg().isEmpty()) {
            String imagePath = imageService.getUserProfileDir();
            imageService.saveProfileImage(request.getProfileImg(), imagePath);
            String image = imageService.getEncodedProfileImage(imagePath, updatedChild.getId());
            response.setProfileImage(image);
        }

        // 프로필 수정에 필요한 시설정보들 가져오기
        Slice<CenterInfoDto> centerInfos = centerRepository.findCenterForAddChild(updatedChild.getCenter().getArea().getSido(),
                updatedChild.getCenter().getArea().getSigungu(), updatedChild.getCenter().getName(), pageable);
        response.setCenterInfoDtoSlice(centerInfos);

        return response;
    }

    /**
    *   작성날짜: 2022/06/28 3:18 PM
    *   작성자: 이승범
    *   작성내용: 아이 삭제
    */
    public ChildInfoDTO deleteChild(Long userId, Long childId) {

        // 요청 사용자가 등록한 모든 아이 가져오기
        List<Child> childrenByUser = childRepository.findByUserWithCenter(userId);

        // 삭제하고자 하는 아이
        Child deletedChild = childrenByUser.stream()
                .filter(child -> Objects.equals(child.getId(), childId))
                .findFirst()
                .orElseThrow(() -> new UserException("잘못된 childId 입니다."));

        // 삭제하고자 하는 아이와 같은 시설에 다니는 또 다른 자녀가 있는지 확인해서 없으면 해당 시설과 관련된 bookmark 모두 삭제
        deleteBookmarkByCenter(userId, childrenByUser, deletedChild);

        childRepository.delete(deletedChild);

        return childrenInfo(userId);
    }

    /**
    *   작성날짜: 2022/06/30 10:36 AM
    *   작성자: 이승범
    *   작성내용: 아이 승인 페이지를 위한 시설에 등록된 아이들 정보 조회
    */
    public ChildApprovalListResponse findChildApprovalInfoList(Long userId) {

        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException("해당 요청에 대한 권한이 없습니다."));

        ChildApprovalListResponse response = new ChildApprovalListResponse();


        teacher.getCenter().getChildren().forEach(child -> {
            // 해당시설에 대해 거절/삭제 당하지 않은 아이들만 보여주기
            if (child.getApproval() != Approval.REJECT) {
                ChildApprovalListResponse.ChildInfoForAdmin childInfo =
                        new ChildApprovalListResponse.ChildInfoForAdmin(child);

                if (child.getHasProfileImg()) {
                    String imagePath = imageService.getChildProfileDir();
                    String image = imageService.getEncodedProfileImage(imagePath, child.getId());
                    childInfo.setChild_profileImg(image);
                }
                response.getData().add(childInfo);
            }
        });
        return response;
    }


    /**
    *   작성날짜: 2022/06/30 3:13 PM
    *   작성자: 이승범
    *   작성내용: 아이 승인
    */
    public void acceptChild(Long userId, Long childId) {

        // 사용자가 등록된 시설과 연관된 아이들 목록 가져오기
        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException("해당 요청에 대한 권한이 없습니다."));

        // childId에 해당하는 아이가 시설에 승인 대기중인지 확인
        Child acceptedChild = teacher.getCenter().getChildren().stream()
                .filter(child -> Objects.equals(child.getId(), childId) && child.getApproval() == Approval.WAITING)
                .findFirst()
                .orElseThrow(() -> new UserException("잘못된 childId 입니다."));

        // 승인
        childRepository.acceptChild(childId, teacher.getCenter().getId());

        // 승인하고자 하는 아이의 부모와 그 부모에 속한 모든 아이들 가져오기
        Parent acceptedParent = parentRepository.findByIdWithChild(acceptedChild.getParent().getId());

        // 승인 완료 알람이 학부모에게로 감
        AlarmUtils.publishAlarmEvent(new CenterApprovalAcceptedAlarm(acceptedParent, teacher.getCenter()));

        // bookmark 처리
        // 기존에 있던 아이들중에 현재 승인되는 아이와 같은 시설에 다니는 또 다른 아이가 있는지 검사
        Optional<Child> alreadySignedChild = acceptedParent.getChildren().stream()
                .filter(child -> Objects.equals(child.getCenter().getId(), teacher.getCenter().getId()))
                .filter(child -> child.getApproval() == Approval.ACCEPT)
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

    }

    /**
    *   작성날짜: 2022/06/30 4:28 PM
    *   작성자: 이승범
    *   작성내용: 시설에서 아이 삭제/승인거절
    */
    public void fireChild(Long userId, Long childId) {

        // 사용자가 등록된 시설과 연관된 아이들 목록 가져오기
        Teacher teacher = teacherRepository.findByIdWithCenterWithChildWithParent(userId)
                .orElseThrow(() -> new UserException("해당 요청에 대한 권한이 없습니다."));

        // childId 검증
        Child firedChild = teacher.getCenter().getChildren().stream()
                .filter(child -> Objects.equals(child.getId(), childId))
                .findFirst()
                .orElseThrow(() -> new UserException("잘못된 childId 입니다."));

        // 시설과의 연관관계 끊기
        childRepository.fireChild(childId);

        // 식제하고자 하는 아이의 부모와 그 부모에 속한 모든 아이들 가져오기
        List<Child> childrenByUser = childRepository.findByUserWithCenter(userId);

        // bookmark 처리
        deleteBookmarkByCenter(userId, childrenByUser, firedChild);
    }

    // 삭제되는 아이와 같은 시설에 다니는 또 다른 아이가 없을경우 해당 시설과 관련된 bookmark 모두 삭제
    private void deleteBookmarkByCenter(Long userId, List<Child> childrenByUser, Child deletedChild) {
        Optional<Child> sameCenterChildren = childrenByUser.stream()
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
            bookmarkRepository.deleteAllByBoardAndUser(userId, boardIds);
        }
    }
}
