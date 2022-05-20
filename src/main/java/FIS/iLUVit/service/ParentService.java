package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ParentDetailResponse;
import FIS.iLUVit.controller.dto.ParentDetailRequest;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.controller.dto.ChildInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final ImageService imageService;

    /**
     *   작성날짜: 2022/05/13 4:43 PM
     *   작성자: 이승범
     *   작성내용: 부모의 메인페이지에 필요한 아이들 정보 반환
     */
    public ChildInfoDTO ChildrenInfo(Long id) {
        Parent findParent = parentRepository.findWithChildren(id)
                .orElseThrow(() -> new UserException("존재하지 않는 User 입니다."));

        ChildInfoDTO childInfoDTO = new ChildInfoDTO();

        findParent.getChildren().forEach(child -> {
            childInfoDTO.getData().add(new ChildInfoDTO.ChildInfo(child));
        });

        return childInfoDTO;
    }

    /**
    *   작성날짜: 2022/05/16 11:42 AM
    *   작성자: 이승범
    *   작성내용: 부모의 마이페이지 정보 업데이트
    */
    public ParentDetailResponse updateDetail(Long id, ParentDetailRequest request) throws IOException {

        Parent findParent = parentRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        // 관심사를 스트링에서 객체로 바꾸기
        ObjectMapper objectMapper = new ObjectMapper();
        Theme theme = objectMapper.readValue(request.getTheme(), Theme.class);

        try {
            parentRepository.findByNickName(request.getNickname()).orElseThrow(IllegalArgumentException::new);
            throw new UserException("이미 존재하는 닉네임 입니다.");
        } catch (IllegalArgumentException e) {
            findParent.updateDetail(request, theme);
        }

        String imagePath = imageService.getUserProfileDir();
        imageService.saveProfileImage(request.getProfileImg(), imagePath + findParent.getId());

        ParentDetailResponse response = new ParentDetailResponse(findParent);
        response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));
        return response;
    }

    /**
    *   작성날짜: 2022/05/13 4:44 PM
    *   작성자: 이승범
    *   작성내용: 부모의 마이페이지 정보 반환
    */
    public ParentDetailResponse findDetail(Long id) throws IOException {

        Parent findParent = parentRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        ParentDetailResponse response = new ParentDetailResponse(findParent);

        String imagePath = imageService.getUserProfileDir();
        response.setProfileImg(imageService.getEncodedProfileImage(imagePath, id));

        return response;
    }
}
