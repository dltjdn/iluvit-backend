package FIS.iLUVit.service;

import FIS.iLUVit.domain.iluvit.Alarm;
import FIS.iLUVit.domain.iluvit.ConvertedToParticipateAlarm;
import FIS.iLUVit.domain.common.Center;
import FIS.iLUVit.domain.iluvit.Participation;
import FIS.iLUVit.domain.iluvit.Presentation;
import FIS.iLUVit.domain.iluvit.PtDate;
import FIS.iLUVit.domain.iluvit.Waiting;
import FIS.iLUVit.dto.presentation.*;
import FIS.iLUVit.domain.iluvit.PresentationCreatedAlarm;
import FIS.iLUVit.domain.common.embeddable.Area;
import FIS.iLUVit.domain.common.embeddable.Theme;
import FIS.iLUVit.domain.iluvit.enumtype.KindOf;
import FIS.iLUVit.domain.iluvit.enumtype.Status;
import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.dto.presentation.PresentationWithPtDatesDto;
import FIS.iLUVit.dto.parent.ParentDto;
import FIS.iLUVit.dto.presentation.PresentationQueryDto;
import FIS.iLUVit.dto.presentation.PtDateDetailDto;
import FIS.iLUVit.repository.common.CenterRepository;
import FIS.iLUVit.repository.iluvit.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
@Slf4j
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final PtDateRepository ptDateRepository;
    private final CenterRepository centerRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final WaitingRepository waitingRepository;
    private final ParticipationRepository participationRepository;

    private final AlarmRepository alarmRepository;

    public List<PresentationDetailResponse> findPresentationByCenterIdAndDate(Long centerId, Long userId) {
        List<PresentationWithPtDatesDto> queryDtos =
                userId == null ? presentationRepository.findByCenterAndDateWithPtDates(centerId, LocalDate.now())
                        : presentationRepository.findByCenterAndDateWithPtDates(centerId, LocalDate.now(), userId);
        return queryDtos.stream().collect(
                        groupingBy(PresentationQueryDto::new,
                                mapping(PtDateDetailDto::new, toList())
                        ))
                .entrySet().stream()
                .map(e -> {
                    PresentationDetailResponse presentationDetailResponse = new PresentationDetailResponse(e.getKey(), imageService.getInfoImages(e.getKey().getInfoImages()),e.getValue());
                    return presentationDetailResponse;
                })
                .collect(toList());
    }


    public Presentation saveInfoWithPtDate(PresentationDetailRequest request, Long userId) {
        if(userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);

        // 리펙터링 필요 findById 를 통해서 그냥 canWrite 와 canRead 를 override 하기
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .canWrite(request.getCenterId());
        if (presentationRepository.findByCenterIdAndDate(request.getCenterId(), LocalDate.now()) != null)
            throw new PresentationException(PresentationErrorResult.ALREADY_PRESENTATION_EXIST);
        Center center = centerRepository.getById(request.getCenterId());
        Presentation presentation = PresentationDetailRequest.toPresentation(request).updateCenter(center);

        request.getPtDateDtos().forEach(ptDateRequestDto -> {
            PtDate.register(presentation,
                    ptDateRequestDto.getDate(),
                    ptDateRequestDto.getTime(),
                    ptDateRequestDto.getAblePersonNum());
        });

        presentationRepository.save(presentation);

        userRepository.getUserPreferByCenterId(center).forEach(prefer -> {
            Alarm alarm = new PresentationCreatedAlarm(prefer.getParent(), presentation, center);
            alarmRepository.save(alarm);
            AlarmUtils.publishAlarmEvent(alarm);
        });

        return presentation;
    }

    public Presentation saveImageWithPtDate(Long presentationId, List<MultipartFile> images, Long userId) {
        if (userId == null)
            throw new UserException(UserErrorResult.NOT_LOGIN);
        Presentation presentation = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.NO_RESULT));

        imageService.saveInfoImages(images, presentation);

        return presentation;
    }

    public List<PresentationForTeacherResponse> findPresentationListByCenterId(Long userId, Long centerId, Pageable pageable) {
        //
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canRead(centerId);
        return presentationRepository.findByCenterId(centerId, pageable)
                .stream().map(data -> {
                    PresentationForTeacherResponse result = new PresentationForTeacherResponse(data,imageService.getInfoImages(data.getPresentationInfoImage()));
                    return result;
                }).collect(toList());
    }

    public PresentationDetailResponse findPresentationDetail(Long presentationId) {
        //
        Presentation presentation = presentationRepository.findByIdAndJoinPtDate(presentationId)
                .orElseThrow(() -> new PresentationException("존재하지않는 설명회 입니다"));
        return new PresentationDetailResponse(presentation, imageService.getInfoImages(presentation));
    }

    public Presentation modifyInfoWithPtDate(PresentationRequest request, Long userId) {
        //
        Presentation presentation = presentationRepository.findByIdAndJoinPtDate(request.getPresentationId())
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.NO_RESULT));
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
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
                    throw new PresentationException(PresentationErrorResult.WRONG_PTDATE_ID_REQUEST);
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
                    ptDate.updateWaitingCntForPtDateChange(waitingIds.size());
                    waitings.forEach(waiting -> {
                        Participation andRegisterForWaitings = Participation.createAndRegisterForWaitings(waiting.getParent(), presentation, ptDate, ptDate.getParticipations());

                        Alarm alarm = new ConvertedToParticipateAlarm(waiting.getParent(), presentation, presentation.getCenter());
                        alarmRepository.save(alarm);
                        AlarmUtils.publishAlarmEvent(alarm);

                        participationRepository.save(andRegisterForWaitings);
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
        presentation.update(request);

        return presentation;
    }

    public Presentation modifyImageWithPtDate(Long presentationId, List<MultipartFile> images, Long userId) {
        //
        Presentation presentation = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.NO_RESULT));

        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST))
                .canWrite(presentation.getCenter().getId());

        imageService.saveInfoImages(images, presentation);

        return presentation;
    }

    public List<ParentDto> findPtDateParticipatingParents(Long userId, Long ptDateId) {
        //
        PtDate ptDate = ptDateRepository.findByIdAndJoinParticipationForSearch(ptDateId)
                .orElseThrow(() -> new PresentationException("존재하지 않는 설명회 회차 입니다."));
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canRead(ptDate.getPresentation().getCenter().getId());
        return ptDate.getParticipations().stream()
                .filter(participation -> participation.getStatus().equals(Status.JOINED))
                .map(participation -> new ParentDto(participation.getParent()))
                .collect(Collectors.toList());

    }

    public List<ParentDto> findPtDateWaitingParents(Long userId, Long ptDateId) {
        //
        PtDate ptDate = ptDateRepository.findByIdWithWaitingAndPresentationAndCenterAndParent(ptDateId)
                .orElseThrow(() -> new PresentationException("존재하지 않는 설명회 회차 입니다."));
        userRepository.findTeacherById(userId)
                .orElseThrow(() -> new UserException("존재하지 않는 유저입니다"))
                .canRead(ptDate.getPresentation().getCenter().getId());

        return ptDate.getWaitings().stream()
                .map(participation -> new ParentDto(participation.getParent()))
                .collect(Collectors.toList());
    }

    public SliceImpl<PresentationForUserResponse> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, String searchContent, Pageable pageable) {
        return presentationRepository.findByFilter(areas, theme, interestedAge, kindOf, searchContent, pageable);
    }
}