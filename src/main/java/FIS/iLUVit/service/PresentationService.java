package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.PresentationRequestRequestFormDto;
import FIS.iLUVit.controller.dto.PresentationResponseDto;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.PresentationRepository;
import FIS.iLUVit.repository.dto.PresentationWithPtDatesDto;
import FIS.iLUVit.service.dto.PresentationQuryDto;
import FIS.iLUVit.service.dto.PtDateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final CenterRepository centerRepository;

    public List<PresentationResponseDto> findPresentationByCenterId(Long centerId) {
        List<PresentationWithPtDatesDto> queryDtos = presentationRepository.findByCenterAndDateWithPtDates(centerId, LocalDate.now());
        return queryDtos.stream().collect(
                groupingBy(queryDto -> new PresentationQuryDto(queryDto),
                        mapping(queryDto -> new PtDateDto(queryDto), toList())
                ))
                .entrySet().stream()
                .map(e -> new PresentationResponseDto(e.getKey(), e.getValue()))
                .collect(toList());
    }

    /**
     * 설명회 저장
     */
    public Presentation saveWithPtDate(PresentationRequestRequestFormDto request) {
        Center center = centerRepository.getById(request.getCenterId());
        Presentation presentation = Presentation.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .content(request.getContent())
                .place(request.getPlace())
                .imgCnt(request.getImgCnt())
                .videoCnt(request.getVideoCnt())
                .center(center)
                .build();

        List<PtDate> ptDates = presentation.getPtDates();

        request.getPtDateDtos().forEach(ptDateRequestDto -> {
            ptDates.add(
                    PtDate.builder()
                            .date(ptDateRequestDto.getDate())
                            .time(ptDateRequestDto.getTime())
                            .ablePersonNum(ptDateRequestDto.getAblePersonNum())
                            .presentation(presentation)
                            .build());
        });

        presentationRepository.save(presentation);
        return presentation;
    }


}