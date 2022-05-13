package FIS.iLUVit.controller;

import FIS.iLUVit.controller.dto.PresentationRequestRequestFormDto;
import FIS.iLUVit.controller.dto.PresentationResponseDto;
import FIS.iLUVit.service.PresentationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PresentationController {

    private final PresentationService presentationService;

    /**
     * 모달창으로 나오는 시설 정보 + 설명회 + 리뷰 정보가 나오는 곳에서 보여줄 설명회에 대한 내용 <p>
     * 현재날짜에 맞춰서 설명회 기간에 있으면 반환 그렇지 않으면 반환 하지않음 <p>
     * 내용 - 신청기간, 내용, 사진, 동영상, 신청할 수 있는 설명회 목록?
     */
    @GetMapping("/presentation/center/{center_id}")
    public List<PresentationResponseDto> findPresentationByCenterId(@PathVariable("center_id") Long id){
        return presentationService.findPresentationByCenterId(id);
    }

    /**
     * 원장/ 선생의 presentation 등록 PtDate 설정하기
     */
    @PostMapping("/presentation")
    public void registerPresentation(@RequestBody PresentationRequestRequestFormDto request){
        presentationService.saveWithPtDate(request);
    }
}
