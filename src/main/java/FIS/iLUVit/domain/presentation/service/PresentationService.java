package FIS.iLUVit.domain.presentation.service;

import FIS.iLUVit.domain.alarm.service.AlarmService;
import FIS.iLUVit.domain.center.domain.Center;
import FIS.iLUVit.domain.center.exception.CenterErrorResult;
import FIS.iLUVit.domain.center.exception.CenterException;
import FIS.iLUVit.domain.center.repository.CenterRepository;
import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.parent.domain.Parent;
import FIS.iLUVit.domain.parent.repository.ParentRepository;
import FIS.iLUVit.domain.participation.domain.Participation;
import FIS.iLUVit.domain.participation.repository.ParticipationRepository;
import FIS.iLUVit.domain.participation.service.ParticipationService;
import FIS.iLUVit.domain.presentation.domain.Presentation;
import FIS.iLUVit.domain.presentation.exception.PresentationErrorResult;
import FIS.iLUVit.domain.presentation.exception.PresentationException;
import FIS.iLUVit.domain.ptdate.domain.PtDate;
import FIS.iLUVit.domain.presentation.dto.*;
import FIS.iLUVit.domain.presentation.repository.PresentationRepository;
import FIS.iLUVit.domain.ptdate.dto.PtDateDetailDto;
import FIS.iLUVit.domain.ptdate.dto.PtDateDto;
import FIS.iLUVit.domain.ptdate.repository.PtDateRepository;
import FIS.iLUVit.domain.teacher.domain.Teacher;
import FIS.iLUVit.domain.teacher.repository.TeacherRepository;
import FIS.iLUVit.domain.user.domain.User;
import FIS.iLUVit.domain.user.exception.UserErrorResult;
import FIS.iLUVit.domain.user.exception.UserException;
import FIS.iLUVit.domain.user.repository.UserRepository;
import FIS.iLUVit.domain.waiting.domain.Waiting;
import FIS.iLUVit.domain.waiting.repository.WaitingRepository;
import FIS.iLUVit.domain.participation.domain.Status;
import FIS.iLUVit.domain.parent.dto.PresentationByParentResponse;
import FIS.iLUVit.domain.common.service.ImageService;
import FIS.iLUVit.domain.waiting.service.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.*;
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
    private final UserRepository userRepository;
    private final AlarmService alarmService;
    private final ParticipationService participationService;
    private final WaitingService waitingService;

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 현재 진행 중인 설명회 리스트 조회
        List<Presentation> presentations = presentationRepository.findByCenterAndDate(centerId, LocalDate.now());

        List<PresentationFindOneResponse> responses = presentations.stream().map((presentation -> {
            List<String> infoImages = imageService.getInfoImages(presentation.getInfoImagePath());
            List<PtDateDetailDto> ptDateDetailDtos = new ArrayList<>();

            if(user.getAuth() == Auth.PARENT){
                // 설명회 별 회차 리스트 조회 및 회차별 대기,참여 명단 조회
                ptDateDetailDtos = ptDateRepository.findByPresentation(presentation).stream()
                        .map((ptDate -> {
                            Waiting waiting = waitingRepository.findByPtDateAndParent(ptDate, (Parent)user).orElse(null);
                            Participation participation = participationRepository.findByPtDateAndParentAndStatus(ptDate, (Parent)user, Status.JOINED).orElse(null);
                            return PtDateDetailDto.of(ptDate, participation, waiting);
                        })).collect(toList());
            }

            if(user.getAuth() == Auth.DIRECTOR || user.getAuth() == Auth.TEACHER){
                ptDateDetailDtos = ptDateRepository.findByPresentation(presentation).stream()
                        .map((ptDate -> {
                            Waiting waiting = null;
                            Participation participation = null;

                            List<Waiting> waitings = waitingRepository.findByPtDate(ptDate);
                            if(!waitings.isEmpty()) waiting = waitings.get(0);

                            List<Participation> participations = participationRepository.findByPtDate(ptDate);
                            if(!participations.isEmpty()) participation = participations.get(0);

                            return PtDateDetailDto.of(ptDate, participation, waiting);
                        })).collect(toList());
            }

            return PresentationFindOneResponse.of(presentation, infoImages, ptDateDetailDtos);
        })).collect(toList());

        return responses;
    }

    /**
     * 설명회 정보 저장 (설명회 회차 정보 저장 포함)
     */
    public PresentationCreateResponse createPresentationInfo(Long userId, PresentationCreateRequest request) {
        Center center = getCenter(request.getCenterId());

        getTeacher(userId, center.getId());  // 글 쓸 권한 있는지 체크

        // 끝나지 않은 설명회 하나라도 있으면 오류
        if(!presentationRepository.findByCenterAndEndDateAfter(center, LocalDate.now()).isEmpty()){
            throw new PresentationException(PresentationErrorResult.VALID_PRESENTATION_ALREADY_EXIST);
        }

        // 설명회, 설명회 회차 생성 및 저장
        Presentation presentation = Presentation.createPresentation(request, center);
        List<PtDate> ptDates = request.getPtDateDtos().stream()
                .map(ptDateDto ->PtDate.createPtDate(presentation, ptDateDto)).collect(toList());
        ptDateRepository.saveAll(ptDates);
        presentationRepository.save(presentation);

        // 시설을 북마크한 사용자에게 '설명회 생성' 알림 전송
        alarmService.sendPresentationCreatedAlarms(center,presentation);

        List<Long> ptDateIds = ptDates.stream().map(PtDate::getId).collect(toList());


        return PresentationCreateResponse.of(presentation,ptDateIds);
    }


    /**
     * 설명회 정보 수정 ( 설명회 회차 정보 수정 포함)
     */
    public PresentationCreateResponse updatePresentationInfo(Long userId, PresentationUpdateRequest request) {

        Presentation presentation = getPresentation(request.getPresentationId());

        getTeacher(userId, presentation.getCenter().getId());

        List<PtDateDto> ptDateDtos = request.getPtDateDtos();

        ptDateDtos.forEach(ptDateDto -> {
                    Long ptDateId = ptDateDto.getPtDateId();

                    if (ptDateId == null) { // 새로운 설명회 회차일 때
                        PtDate ptDate = PtDate.createPtDate(presentation, ptDateDto);
                        ptDateRepository.save(ptDate);

                    } else { // 기존 설명회 회차 정보를 수정할 때
                        PtDate ptDate = ptDateRepository.findById(ptDateId)
                                .orElseThrow(() -> new PresentationException(PresentationErrorResult.PTDATE_NOT_FOUND));

                        // 수정 정보의 수용가능 인원이 현재 수용가능 인원보다 많고, 대기자가 있을 때
                        if (ptDateDto.getAblePersonNum() > ptDate.getAblePersonNum() && ptDate.checkHasWaiting()) {
                            moveWaitingListToParticipants(presentation, ptDateDto, ptDate);
                        }

                        ptDate.updatePtDate(ptDateDto); // 설명회 회차 정보를 업데이트
                    }
                }
        );

        // 설명회 정보 업데이트
        presentation.updatePresentation(request);

        List<Long> ptDateIds = ptDateDtos.stream()
                .map(PtDateDto::getPtDateId)
                .collect(toList());

        return PresentationCreateResponse.of(presentation, ptDateIds);

    }


    /**
     * 설명회 이미지 저장 및 수정
     */
    public PresentationCreateResponse updatePresentationImage(Long userId, Long presentationId, List<MultipartFile> images) {
        Presentation presentation = getPresentation(presentationId);

        getTeacher(userId,presentation.getCenter().getId());

        imageService.saveInfoImages(images, presentation);
        return PresentationCreateResponse.of(presentation, null);
    }

    /**
     * 교사용 설명회 전체 조회
     */
    public List<PresentationForTeacherResponse> findPresentationListByCenter(Long userId, Long centerId, Pageable pageable) {

        getTeacher(userId,centerId);

        Center center = getCenter(centerId);

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
        getTeacher(userId, ptDate.getPresentation().getCenter().getId());

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
        getTeacher(userId, ptDate.getPresentation().getCenter().getId());

        List<PresentationByParentResponse> responses = waitingRepository.findByPtDate(ptDate).stream()
                .map(Waiting::getParent)
                .map(PresentationByParentResponse::of)
                .collect(toList());

        return responses;
    }

    private void moveWaitingListToParticipants(Presentation presentation, PtDateDto ptDateDto, PtDate ptDate) {
        int capacityNum = ptDateDto.getAblePersonNum() - ptDate.getAblePersonNum();  // 추가 수용 가능 인원

        // 추가 수용될 wiating 리스트 조회
        List<Waiting> waitings = waitingRepository.findByPtDateAndWaitingOrderLessThanEqual(ptDate, capacityNum);

        waitingService.updateWaitingOrdersAndDeleteWaitings(waitings,capacityNum, ptDate);

        ptDate.updateWaitingCntForPtDateChange(waitings.size());

        waitings.forEach(waiting ->
                participationService.saveParticipation(waiting.getParent(),presentation,ptDate));

        alarmService.sendParticipateAlarms(waitings, presentation);
    }

    /**
     * 예외처리 - 존재하는 권한있는 선생님인가
     */
    private Teacher getTeacher(Long userId, Long centerId) {
        return teacherRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND))
                .checkPermission(centerId);
    }

    /**
     * 예외처리 - 존재하는 설명회인가
     */
    private Presentation getPresentation(Long presentationId) {
        return presentationRepository.findById(presentationId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.PRESENTATION_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 설명회회차인가
     */
    private PtDate getPtDate(Long ptDateId) {
        return ptDateRepository.findById(ptDateId)
                .orElseThrow(() -> new PresentationException(PresentationErrorResult.PTDATE_NOT_FOUND));
    }

    /**
     * 예외처리 - 존재하는 시설인가
     */
    private Center getCenter(Long centerId) {
        return centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_FOUND));
    }

}