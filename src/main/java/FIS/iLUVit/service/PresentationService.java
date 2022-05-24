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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final CenterRepository centerRepository;
    private final ImageService imageService;

    public List<PresentationResponseDto> findPresentationByCenterId(Long centerId) {
        List<PresentationWithPtDatesDto> queryDtos = presentationRepository.findByCenterAndDateWithPtDates(centerId, LocalDate.now());
        return queryDtos.stream().collect(
                groupingBy(queryDto -> new PresentationQuryDto(queryDto),
                        mapping(queryDto -> new PtDateDto(queryDto), toList())
                ))
                .entrySet().stream()
                .map(e -> {
                    PresentationResponseDto presentationResponseDto = new PresentationResponseDto(e.getKey(), e.getValue());
                    Long presentationId = presentationResponseDto.getPresentationId();
                    String presentationDir = imageService.getPresentationDir(presentationId);
                    presentationResponseDto.setImages(
                            imageService.getEncodedInfoImage(presentationDir, presentationResponseDto.getImgCnt()));
                    return presentationResponseDto;
                })
                .collect(toList());
    }

    /**
     * 설명회 저장
     */
    public Presentation saveWithPtDate(PresentationRequestRequestFormDto request, List<MultipartFile> images) {
        Center center = centerRepository.getById(request.getCenterId());
        Presentation presentation = PresentationRequestRequestFormDto.toPresentation(request);
        presentation.updateImageCnt(images.size())
                .updateCenter(center);
        List<PtDate> ptDates = presentation.getPtDates();

        request.getPtDateDtos().forEach(ptDateRequestDto -> {
            ptDates.add(PtDate
                    .register(presentation,
                            ptDateRequestDto.getDate(),
                            ptDateRequestDto.getTime(),
                            ptDateRequestDto.getAblePersonNum()));
        });

        presentationRepository.save(presentation);
        String presentationDir = imageService.getPresentationDir(presentation.getId());
        imageService.saveInfoImage(images, presentationDir);

        return presentation;
    }


}