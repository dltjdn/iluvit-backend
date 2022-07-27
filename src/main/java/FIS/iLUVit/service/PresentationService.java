package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.alarms.PresentationCreatedAlarm;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.repository.dto.PresentationPreviewForUsers;
import FIS.iLUVit.repository.dto.PresentationWithPtDatesDto;
import FIS.iLUVit.service.dto.ParentInfoForDirectorDto;
import FIS.iLUVit.service.dto.PresentationQuryDto;
import FIS.iLUVit.service.dto.PtDateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final PtDateRepository ptDateRepository;
    private final CenterRepository centerRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final WaitingRepository waitingRepository;

    public List<PresentationResponseDto> findPresentationByCenterIdAndDate(Long centerId, Long userId) {
        List<PresentationWithPtDatesDto> queryDtos =
                userId == null ? presentationRepository.findByCenterAndDateWithPtDates(centerId, LocalDate.now())
                : presentationRepository.findByCenterAndDateWithPtDates(centerId, LocalDate.now(), userId);
        return queryDtos.stream().collect(
                groupingBy(PresentationQuryDto::new,
                        mapping(PtDateDto::new, toList())
                ))
                .entrySet().stream()
                .map(e -> {
                    PresentationResponseDto presentationResponseDto = new PresentationResponseDto(e.getKey(), e.getValue());
                    presentationResponseDto.setImages(imageService.getInfoImages(e.getKey().getInfoImages()));
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
        Presentation presentation = PresentationRequestRequestFormDto.toPresentation(request).updateCenter(center);

        request.getPtDateDtos().forEach(ptDateRequestDto -> {
            PtDate.register(presentation,
                            ptDateRequestDto.getDate(),
                            ptDateRequestDto.getTime(),
                            ptDateRequestDto.getAblePersonNum());
        });

        imageService.saveInfoImages(images, presentation);
        presentationRepository.save(presentation);

        userRepository.getUserPreferByCenterId(center).forEach(prefer -> {
            log.info("알림 메시지 생성 {}", prefer.getParent().getId());
            AlarmUtils.publishAlarmEvent(new PresentationCreatedAlarm(prefer.getParent(), presentation, center));
        });

        return presentation;
    }

    public List<PresentationPreviewAndImageForTeacher> findPresentationListByCenterId(Long userId, Long centerId, Pageable pageable) {
        //
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canRead(centerId);
        return presentationRepository.findByCenterId(centerId, pageable)
                .stream().map(data -> {
                    PresentationPreviewAndImageForTeacher result = new PresentationPreviewAndImageForTeacher(data);
                    result.setPresentationInfoImage(imageService.getInfoImages(data.getPresentationInfoImage()));
                    return result;
                }).collect(toList());
    }

    public PresentationResponseDto findPresentationDetail(Long presentationId) {
        //
        Presentation presentation = presentationRepository.findByIdAndJoinPtDate(presentationId)
                .orElseThrow(() -> new PresentationException("존재하지않는 설명회 입니다"));
        return new PresentationResponseDto(presentation, imageService.getInfoImages(presentation));
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
                if(ptDateModifyDto.getAblePersonNum() > ptDate.getAblePersonNum() && ptDate.hasWaiting()){
                    // 추가 수용 가능 인원 숫자 체크
                    Integer changeNum = ptDateModifyDto.getAblePersonNum() - ptDate.getAblePersonNum();
                    // 추가 수용될 인원 추출
                    List<Waiting> waitings = waitingRepository.findWaitingsByPtDateAndOrderNum(ptDate, changeNum);
                    // 추가 수용될 인원 id 만 추출
                    List<Long> waitingIds = waitings.stream().map(Waiting::getId).collect(toList());
                    // 수용 인원들 waiting 에서 삭제
                    waitingRepository.deleteAllByIdInBatch(waitingIds);
                    // 수용 외의 인원들 order 감소
                    waitingRepository.updateWaitingOrderForPtDateChange(changeNum, ptDate);
                    ptDate.updateWaitingCntForPtDateChange(changeNum);
                    waitings.forEach(waiting -> {
                        Participation.createAndRegisterForWaitings(waiting.getParent(), presentation, ptDate, ptDate.getParticipations());
                    });
                }
                ptDate.update(ptDateModifyDto);
                ptDateMap.remove(ptDate.getId());
            }
        });

        Set<Long> ptDateKeysDeleteTarget = ptDateMap.keySet();
        Collection<PtDate> ptDateSet = ptDateMap.values();

        ptDateSet.forEach(PtDate::canDelete);
        presentation.getPtDates().removeAll(ptDateSet);
        ptDateRepository.deletePtDateByIds(ptDateKeysDeleteTarget);
        imageService.saveInfoImages(images, presentation);
        presentation.update(request);

        return presentation;
    }

    public List<ParentInfoForDirectorDto> findPtDateParticipatingParents(Long userId, Long ptDateId) {
        //
        PtDate ptDate = ptDateRepository.findByIdAndJoinParticipationForSearch(ptDateId)
                .orElseThrow(() -> new PresentationException("존재하지 않는 설명회 회차 입니다."));
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canRead(ptDate.getPresentation().getCenter().getId());
        return ptDate.getParticipations().stream()
                .filter(participation -> participation.getStatus().equals(Status.JOINED))
                .map(participation -> new ParentInfoForDirectorDto(participation.getParent()))
                .collect(Collectors.toList());

    }

    public List<ParentInfoForDirectorDto> findPtDateWaitingParents(Long userId, Long ptDateId) {
        //
        PtDate ptDate = ptDateRepository.findByIdWithWaitingAndPresentationAndCenterAndParent(ptDateId)
                .orElseThrow(() -> new PresentationException("존재하지 않는 설명회 회차 입니다."));
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canRead(ptDate.getPresentation().getCenter().getId());

        return ptDate.getWaitings().stream()
                .map(participation -> new ParentInfoForDirectorDto(participation.getParent()))
                .collect(Collectors.toList());
    }

    public SliceImpl<PresentationPreviewForUsersResponse> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, Pageable pageable) {
        return presentationRepository.findByFilter(areas, theme, interestedAge, kindOf, pageable);
    }
}