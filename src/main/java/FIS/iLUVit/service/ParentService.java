package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ParentDetailDTO;
import FIS.iLUVit.controller.dto.ParentDetailRequest;
import FIS.iLUVit.domain.Child;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.controller.dto.ChildInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;

    @Value("${profileImg.path}")
    private String profileImgPath;

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
    public ParentDetailDTO updateDetail(Long id, ParentDetailRequest request) {

        Parent findParent = parentRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));
        return null;
    }

    /**
    *   작성날짜: 2022/05/13 4:44 PM
    *   작성자: 이승범
    *   작성내용: 부모의 마이페이지 정보 반환
    */
    public ParentDetailDTO findDetail(Long id) throws IOException {

        Parent findParent = parentRepository.findById(id)
                .orElseThrow(() -> new UserException("유효하지 않은 토큰으로의 사용자 접근입니다."));

        ParentDetailDTO response = new ParentDetailDTO(findParent);

        // 부모의 프로필 사진 담기
        InputStream imageStream = new FileInputStream(profileImgPath + id + ".png");
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        String encodedImage = Base64.encodeBase64String(imageByteArray);
        response.setProfileImg(encodedImage);

        return response;
    }

}
