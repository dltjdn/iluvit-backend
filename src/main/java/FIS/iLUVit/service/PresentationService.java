package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.PresentationModifyRequestDto;
import FIS.iLUVit.controller.dto.PresentationRequestRequestFormDto;
import FIS.iLUVit.controller.dto.PresentationResponseDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
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

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        // 리펙터링 필요 findById 를 통해서 그냥 canWrite 와 canRead 를 override 하기
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canWrite(request.getCenterId());
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

    public List<PresentationPreviewDto> findPresentationListByCenterId(Long userId, Long centerId) {
        //
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canRead(centerId);
        return presentationRepository.findByCenterId(centerId);
    }

    public PresentationResponseDto findPresentationDetail(Long presentationId) {
        //
        Presentation presentation = presentationRepository.findByIdAndJoinPtDate(presentationId)
                .orElseThrow(() -> new PresentationException("존재하지않는 설명회 입니다"));
        String presentationDir = imageService.getPresentationDir(presentationId);
        List<String> encodedInfoImage = imageService.getEncodedInfoImage(presentationDir, presentation.getImgCnt());
        return new PresentationResponseDto(presentation, encodedInfoImage);
    }

    public Presentation modifyWithPtDate(PresentationModifyRequestDto request, List<MultipartFile> images, Long userId) {
        //
        Presentation presentation = presentationRepository.findByIdAndJoinPtDate(request.getPresentationId())
                .orElseThrow(() -> new PresentationException("존재하지 않는 설명회 입니다."));
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canWrite(presentation.getCenter().getId());

        // 데이터 베이스에 저장되어있는 ptDate 목록
        Map<Long, PtDate> ptDateMap = presentation.getPtDates()
                .stream()
                .collect(toMap(PtDate::getId,
                        ptDate -> ptDate));

        // modify 요청에서 넘어온 ptdate 정보
        request.getPtDateDtos().forEach(ptDateModifyDto -> {
            if(ptDateModifyDto.getPtDateId() == null) {
                PtDate register = PtDate.register(presentation,
                        ptDateModifyDto.getDate(),
                        ptDateModifyDto.getTime(),
                        ptDateModifyDto.getAblePersonNum());
                ptDateRepository.save(register);
            }
            else {
                PtDate ptDate = ptDateMap.get(ptDateModifyDto.getPtDateId());
                if(ptDate == null)
                    throw new PresentationException("잘못된 접근입니다");
                ptDate.update(ptDateModifyDto);
                ptDateMap.remove(ptDate.getId());
            }
        });

        Set<Long> ptDateKeysDeleteTarget = ptDateMap.keySet();
        Collection<PtDate> ptDateSet = ptDateMap.values();
        ptDateSet.forEach(ptDate -> ptDate.canDelete());
        presentation.getPtDates().removeAll(ptDateSet);
        ptDateRepository.deletePtDateByIds(ptDateKeysDeleteTarget);
        presentation.update(request, images.size(), 0);
        String presentationDir = imageService.getPresentationDir(presentation.getId());
        imageService.saveInfoImage(images, presentationDir);
        return presentation;
    }

    public List<ParentInfoForDirectorDto> findPtDateParticipatingParents(Long userId, Long ptDateId) {
        //
        PtDate ptDate = ptDateRepository.findByIdAndJoinParticipation(ptDateId)
                .orElseThrow(() -> new PresentationException("존재하지 않는 설명회 회차 입니다."));
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canRead(ptDate.getPresentation().getCenter().getId());
        return ptDate.getParticipations().stream()
                .map(participation -> new ParentInfoForDirectorDto(participation.getParent()))
                .collect(Collectors.toList());

    }

    public List<ParentInfoForDirectorDto> findPtDateWaitingParents(Long userId, Long ptDateId) {
        //
        PtDate ptDate = ptDateRepository.findByIdAndJoinWaiting(ptDateId)
                .orElseThrow(() -> new PresentationException("존재하지 않는 설명회 회차 입니다."));
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canRead(ptDate.getPresentation().getCenter().getId());

        return ptDate.getWaitings().stream()
                .map(participation -> new ParentInfoForDirectorDto(participation.getParent()))
                .collect(Collectors.toList());
    }

    public void findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable) {
        presentationRepository.findByFilter(areas, theme, interestedAge, kindOf, pageable);
    }
}