package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Parent;
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

    private final ParentRepository parentRepository;
    private final BookmarkService bookmarkService;
    private final CenterRepository centerRepository;
    private final ChildRepository childRepository;
    private final ImageService imageService;
    private final BoardRepository boardRepository;
    private final BookmarkRepository bookmarkRepository;

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
        Center center = centerRepository.findByIdAndSignedWithBoard(request.getCenter_id(), true)
                .orElseThrow(() -> new CenterException("잘못된 centerId 입니다."));

        // bookmark 처리
//        // 기존에 있던 아이들중에 현재 등록하려는 아이와 같은 시설에 다니는 아이가 있는지 검사
//        Optional<Child> alreadySignedChild = parent.getChildren().stream()
//                .filter(child -> child.getCenter().getId().equals(center.getId()))
//                .findFirst();
//
//
//        // 기존 아이들과 다른 시설에 아이를 등록할 경우 해당 시설에 default board 북마크 추가
//        if (alreadySignedChild.isEmpty()) {
//            center.getBoards().forEach(board -> {
//                if (board.getIsDefault()) {
//                    bookmarkService.create(parent.getId(), board.getId());
//                }
//            });
//        }

        // 아이 등록
        Child newChild = request.createChild(center, parent);
        childRepository.save(newChild);

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

        Child child = childRepository.findByIdWithParentAndCenter(userId, childId)
                .orElseThrow(() -> new UserException("잘못된 child_id 입니다."));

        Center center = centerRepository.getById(request.getCenter_id());
        child.update(center, request.getName(), request.getBirthDate(), request.getProfileImg());

        ChildInfoDetailResponse response = new ChildInfoDetailResponse(child);

        // 이미지가 있는 경우
        if (request.getProfileImg() != null) {
            String imagePath = imageService.getUserProfileDir();
            imageService.saveProfileImage(request.getProfileImg(), imagePath);
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

        // 삭제하고자 하는 아이가 등록된 시설
        Center belongedCenter = deletedChild.getCenter();

        // 삭제하고자 하는 아이와 같은 시설에 다니는 또 다른 자녀가 있는지 확인
        List<Child> sameCenterChildren = childrenByUser.stream()
                .filter(child -> child.getCenter() == belongedCenter)
                .filter(child-> !Objects.equals(child.getId(), deletedChild.getId()))
                .filter(child-> child.getApproval() == Approval.ACCEPT)
                .collect(Collectors.toList());

        // 없으면 해당 시설과 연관된 bookMark 싹 다 삭제
        if (sameCenterChildren.isEmpty()) {
            List<Board> boards = boardRepository.findByCenter(belongedCenter.getId());
            List<Long> boardIds = boards.stream()
                    .map(Board::getId)
                    .collect(Collectors.toList());
            bookmarkRepository.deleteAllByBoardAndUser(userId, boardIds);
        }

        childRepository.delete(deletedChild);

        return childrenInfo(userId);
    }
}
