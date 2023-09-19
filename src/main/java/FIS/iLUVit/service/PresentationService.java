package FIS.iLUVit.service;

import FIS.iLUVit.domain.alarms.Alarm;
import FIS.iLUVit.domain.alarms.ConvertedToParticipateAlarm;
import FIS.iLUVit.dto.presentation.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Status;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import FIS.iLUVit.dto.parent.PresentationByParentResponse;
import FIS.iLUVit.dto.presentation.PtDateDetailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    private final TeacherRepository teacherRepository;
    private final WaitingRepository waitingRepository;
    private final ParticipationRepository participationRepository;
    private final AlarmRepository alarmRepository;
    private final ParentRepository parentRepository;
    private final AlarmService alarmService;

    /**
     * 필터 기반 설명회 검색
     */
    public Slice<PresentationSearchFilterResponse> findPresentationByFilter(PresentationSearchFilterRequest request, Pageable pageable) {

        Slice<Presentation> presentations = presentationRepository.findByFilter(request.getAreas(), request.getTheme(),request.getInterestedAge(), request.getKindOf(), request.getSearchContent(), pageable);

        Slice<PresentationSearchFilterResponse> responses = presentations
                .map(presentation -> {
                    List<String> infoImages = imageService.getInfoImages(presentation.getInfoImagePath());
                    return PresentationSearchFilterResponse.of(presentation, infoImages);
        });

        return responses;
    }

    /**
     * (현재 진행 중인) 설명회 전체 조회
     */
    public List<PresentationFindOneResponse> findAllPresentation(Long userId, Long centerId) {
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 현재 진행 중인 설명회 리스트 조회
        List<Presentation> presentations = presentationRepository.findByCenterAndDate(centerId, LocalDate.now());

        List<PresentationFindOneResponse> responses = presentations.stream().map((presentation -> {
            List<String> infoImages = imageService.getInfoImages(presentation.getInfoImagePath());

            // 설명회 별 회차 리스트 조회 및 회차별 대기,참여 명단 조회
            List<PtDateDetailDto> ptDateDetailDtos = ptDateRepository.findByPresentation(presentation).stream()
                    .map((ptDate -> {
                        Waiting waitings = waitingRepository.findByPtDateAndParent(ptDate, parent).orElse(null);
                        Participation participations = participationRepository.findByPtDateAndParentAndStatus(ptDate, parent, Status.JOINED).orElse(null);
                        return PtDateDetailDto.of(ptDate, participations, waitings);
                    })).collect(toList());

            return PresentationFindOneResponse.of(presentation, infoImages, ptDateDetailDtos);
        })).collect(toList());

        return responses;
    }

    /**
     * 설명회 정보 저장 (설명회 회차 정보 저장 포함)
     */
    public PresentationCreateResponse createPresentationInfo(Long userId, PresentationCreateRequest request) {
        Center center = centerRepository.findById(request.getCenterId())
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        getTeacher(userId).canWrite(center.getId());  // 글 쓸 권한 있는지 체크

        // 끝나지 않은 설명회 하나라도 있으면 오류
        presentationRepository.findByCenterAndEndDateAfter(center, LocalDate.now())
                .ifPresent((presentation) -> new PresentationException(PresentationErrorResult.PRESENTATION_ALREADY_EXIST));

        // 설명회, 설명회 회차 생성 및 저장
        Presentation presentation = Presentation.createPresentation(request, center);
        List<PtDate> ptDates = request.getPtDateDtos().stream()
                .map(ptDateDto ->PtDate.createPtDate(presentation, ptDateDto)).collect(toList());
        ptDateRepository.saveAll(ptDates);
        presentationRepository.save(presentation);

        // 시설을 북마크한 사용자에게 '설명회 생성' 알림 전송
        alarmService.sendPresentationCreatedAlarm(center,presentation);

        List<Long> ptDateIds = ptDates.stream().map(PtDate::getId).collect(toList());

        return PresentationCreateResponse.of(presentation,ptDateIds);
    }


    /**
     * 설명회 정보 수정 ( 설명회 회차 정보 수정 포함)
     */
    public void updatePresentationInfo(Long userId, PresentationUpdateRequest request) {

        Presentation presentation = getPresentation(request.getPresentationId());

        getTeacher(userId).canWrite(presentation.getCenter().getId());

        // 데이터 베이스에 저장되어있는 ptDate 목록
        Map<Long, PtDate> ptDateMap = presentation.getPtDates()
                .stream()
                .collect(toMap(PtDate::getId,
                        ptDate -> ptDate));

        // requst의 ptdate 정보
        request.getPtDateDtos().forEach(ptDateModifyDto -> {
            if(ptDateModifyDto.getPtDateId() == null) {
                PtDate register = PtDate.createPtDate(presentation, ptDateModifyDto);
                ptDateRepository.save(register);
            }
            else {
                PtDate ptDate = ptDateMap.get(ptDateModifyDto.getPtDateId());
                if(ptDate == null)
                    throw new PresentationException(PresentationErrorResult.WRONG_PTDATE_REQUEST);
                if(ptDateModifyDto.getAblePersonNum() > ptDate.getAblePersonNum() && ptDate.hasWaiting()){
                    // 추가 수용 가능 인원 숫자 체크
                    Integer changeNum = ptDateModifyDto.getAblePersonNum() - ptDate.getAblePersonNum();
                    // 추가 수용될 인원 추출
                    List<Waiting> waitings = waitingRepository.findByPtDateAndWaitingOrderLessThanEqual(ptDate, changeNum);
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
                        String type = "아이러빗";
                        AlarmUtils.publishAlarmEvent(alarm, type);

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

    }

    /**
     * 설명회 이미지 저장 및 수정
     */
    public void updatePresentationImage(Long userId, Long presentationId, List<MultipartFile> images) {
        Presentation presentation = getPresentation(presentationId);

        getTeacher(userId).canWrite(presentation.getCenter().getId());

        imageService.saveInfoImages(images, presentation);
    }

    /**
     * 교사용 설명회 전체 조회
     */
    public List<PresentationForTeacherResponse> findPresentationListByCenter(Long userId, Long centerId, Pageable pageable) {

        getTeacher(userId).canRead(centerId);

        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        List<PresentationForTeacherResponse> response = presentationRepository.findByCenter(center).stream()
                .map(presentation -> {
                    List<String> infoImages = imageService.getInfoImages(presentation.getInfoImagePath());
                    return PresentationForTeacherResponse.of(presentation, infoImages);
                })
                .collect(toList());

        return response;
    }

    /**
     * 설명회 상세 조회
     */
    public PresentationFindOneResponse findPresentationDetails(Long presentationId) {
        Presentation presentation = getPresentation(presentationId);

        List<String> infoImages = imageService.getInfoImages(presentation.getInfoImagePath());

        List<PtDateDetailDto> ptDateDetailDtos = ptDateRepository.findByPresentation(presentation).stream()
                .map(PtDateDetailDto::from)
                .collect(toList());

        return PresentationFindOneResponse.of(presentation, infoImages, ptDateDetailDtos);
    }

    /**
     * 설명회 예약 학부모 전체 조회 (예약명단)
     */
    public List<PresentationByParentResponse> findParentListWithRegisterParticipation(Long userId, Long ptDateId) {
        PtDate ptDate = getPtDate(ptDateId);
        getTeacher(userId).canRead(ptDate.getPresentation().getCenter().getId());

        List<PresentationByParentResponse> responses = participationRepository.findByPtDateAndStatus(ptDate, Status.JOINED).stream()
                .map(Participation::getParent)
                .map(PresentationByParentResponse::of)
                .collect(toList());

        return responses;
    }

    /**
     * 설명회 대기 학부모 전체 조회 (대기명단)
     */
    public List<PresentationByParentResponse> findParentListWithWaitingParticipation(Long userId, Long ptDateId) {
        PtDate ptDate = getPtDate(ptDateId);
        getTeacher(userId).canRead(ptDate.getPresentation().getCenter().getId());

        List<PresentationByParentResponse> responses = waitingRepository.findByPtDate(ptDate).stream()
                .map(Waiting::getParent)
                .map(PresentationByParentResponse::of)
                .collect(toList());

        return responses;
    }

    /**
     * 예외처리 - 존재하는 선생님인가
     */
    private Teacher getTeacher(Long userId) {
        return teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
    }

    /**
     * 예외처리 - 존재하는 설명회인가
     */
    private Presentation getPresentation(Long presentationId) {
        return presentationRepository.findById(presentationId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.PRESENTATION_NOT_EXIST));
    }

    /**
     * 예외처리 - 존재하는 설명회회차인가
     */
    private PtDate getPtDate(Long ptDateId) {
        return ptDateRepository.findById(ptDateId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.PTDATE_NOT_EXIST));

    }


}