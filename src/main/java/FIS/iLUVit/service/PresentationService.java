package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.PresentationModifyRequestDto;
import FIS.iLUVit.controller.dto.PresentationRequestRequestFormDto;
import FIS.iLUVit.controller.dto.PresentationResponseDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.PresentationRepository;
import FIS.iLUVit.repository.PtDateRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.repository.dto.PresentationPreviewDto;
import FIS.iLUVit.repository.dto.PresentationWithPtDatesDto;
import FIS.iLUVit.service.dto.ParentInfoForDirectorDto;
import FIS.iLUVit.service.dto.PresentationQuryDto;
import FIS.iLUVit.service.dto.PtDateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@Transactional
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final PtDateRepository ptDateRepository;
    private final CenterRepository centerRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;

    public List<PresentationResponseDto> findPresentationByCenterIdAndDate(Long centerId) {
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
    public Presentation saveWithPtDate(PresentationRequestRequestFormDto request, List<MultipartFile> images, Long userId) {
        userRepository.findTeacherById(userId).orElseThrow(() -> new UserException("존재하지 않는 유저입니다")).canWrite();
        if (presentationRepository.findByCenterIdAndDate(request.getCenterId(), LocalDate.now()) != null)
            throw new PresentationException("아직 유효한 설명회가 있습니다");
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

    public List<PresentationPreviewDto> findPresentationListByCenterId(Long centerId) {
        return presentationRepository.findByCenterId(centerId);
    }

    public void findPresentationDetail(Long presentationId, Long userId) {
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다")).canWrite();
        Presentation presentation = presentationRepository.findByIdAndJoinPtDate(presentationId)
                .orElseThrow(() -> new PresentationException("존재하지않는 설명회 입니다"));
        String presentationDir = imageService.getPresentationDir(presentationId);
        List<String> encodedInfoImage = imageService.getEncodedInfoImage(presentationDir, presentation.getImgCnt());
        new PresentationResponseDto(presentation, encodedInfoImage);
    }

    public Presentation modifyWithPtDate(PresentationModifyRequestDto request, List<MultipartFile> images, Long userId) {
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다")).canWrite();
        Presentation presentation = presentationRepository.findByIdAndJoinPtDate(request.getPresentationId())
                .orElseThrow(() -> new PresentationException("존재하지 않는 설명회 입니다."));
        Map<Long, PtDate> ptDateMap = presentation.getPtDates()
                .stream()
                .collect(toMap(PtDate::getId,
                        ptDate -> ptDate));
        request.getPtDateDtos().forEach(ptDateModifyDto -> {
            if(ptDateModifyDto.getPtDateId() == null) {
                PtDate register = PtDate.register(presentation,
                        ptDateModifyDto.getDate(),
                        ptDateModifyDto.getTime(),
                        ptDateModifyDto.getAblePersonNum());
                ptDateRepository.save(register);
            }
            PtDate ptDate = ptDateMap.get(ptDateModifyDto.getPtDateId());
            ptDate.update(ptDateModifyDto);
        });
        presentation.update(request, images.size(), 0);
        String presentationDir = imageService.getPresentationDir(presentation.getId());
        imageService.saveInfoImage(images, presentationDir);
        return presentation;
    }

    public List<ParentInfoForDirectorDto> findPtDateParticipatingParents(Long userId, Long ptDateId) {
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다")).canRead();
        PtDate ptDate = ptDateRepository.findByIdAndJoinParticipation(ptDateId)
                .orElseThrow(() -> new PresentationException("존재하지 않는 설명회 회차 입니다."));
        return ptDate.getParticipations().stream()
                .map(participation -> new ParentInfoForDirectorDto(participation.getParent()))
                .collect(Collectors.toList());

    }

    public List<ParentInfoForDirectorDto> findPtDateWaitingParents(Long userId, Long ptDateId) {
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다")).canRead();
        PtDate ptDate = ptDateRepository.findByIdAndJoinWaiting(ptDateId)
                .orElseThrow(() -> new PresentationException("존재하지 않는 설명회 회차 입니다."));
        return ptDate.getWaitings().stream()
                .map(participation -> new ParentInfoForDirectorDto(participation.getParent()))
                .collect(Collectors.toList());
    }
}