package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.SaveChildRequest;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.ChildRepository;
import FIS.iLUVit.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

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
        // 기존에 있던 아이들중에 현재 등록하려는 아이와 같은 시설에 다니는 아이가 있는지 검사
        Optional<Child> alreadySignedChild = parent.getChildren().stream()
                .filter(child -> child.getCenter().getId().equals(center.getId()))
                .findFirst();
        // 기존 아이들과 다른 시설에 아이를 등록할 경우 해당 시설에 default board 북마크 추가
        if (alreadySignedChild.isEmpty()) {
            center.getBoards().forEach(board -> {
                if (board.getIsDefault()) {
                    bookmarkService.create(parent.getId(), board.getId());
                }
            });
        }

        // 아이 등록
        Child newChild = request.createChild(center, parent);
        childRepository.save(newChild);

        // 프로필 이미지 설정
        if (!request.getProfileImg().isEmpty()) {
            String imagePath = imageService.getChildProfileDir();
            imageService.saveProfileImage(request.getProfileImg(), imagePath + newChild.getId());
        }
    }
}
