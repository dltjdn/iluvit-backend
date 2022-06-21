package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.ScrapListInfoResponse;
import FIS.iLUVit.controller.dto.addScrapRequest;
import FIS.iLUVit.domain.Scrap;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.ScrapException;
import FIS.iLUVit.repository.ScrapRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final UserRepository userRepository;

    /**
    *   작성날짜: 2022/06/21 2:03 PM
    *   작성자: 이승범
    *   작성내용: 스크랩 폴더 목록 가져오기
    */
    public ScrapListInfoResponse findScrapListInfo(Long id) {
        List<Scrap> scraps = scrapRepository.findScrapsWithScrapPostsByUser(id);
        ScrapListInfoResponse response = new ScrapListInfoResponse();

        scraps.forEach(scrap -> {
            response.getData().add(new ScrapListInfoResponse.ScrapInfo(scrap));
        });
        return response;
    }

    /**
    *   작성날짜: 2022/06/21 2:11 PM
    *   작성자: 이승범
    *   작성내용: 스크랩 폴더 추가하기
    */
    public ScrapListInfoResponse addScrap(Long id, addScrapRequest request) {
        User user = userRepository.getById(id);
        Scrap newScrap = Scrap.createScrap(user, request.getName());
        scrapRepository.save(newScrap);
        // 스크랩 파일을 추가한 상태의 전체 스크랩 파일 목록 가져오기
        return findScrapListInfo(id);
    }

    /**
    *   작성날짜: 2022/06/21 2:59 PM
    *   작성자: 이승범
    *   작성내용: 스크랩 폴더 삭제하기
    */
    public ScrapListInfoResponse deleteScrap(Long id, Long scrapId) {
        try {
            scrapRepository.deleteById(scrapId);
        } catch (EmptyResultDataAccessException e) {
            throw new ScrapException("존재하지 않는 scrapId 입니다.");
        }
        return findScrapListInfo(id);
    }
}
