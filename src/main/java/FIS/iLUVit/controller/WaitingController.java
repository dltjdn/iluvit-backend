package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.Login;
import FIS.iLUVit.controller.dto.WaitingCancelDto;
import FIS.iLUVit.controller.dto.WaitingRegisterDto;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Teacher;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.event.AlarmEvent;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.UserRepository;
import FIS.iLUVit.service.WaitingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WaitingController {

    private final WaitingService waitingService;
    private final ApplicationEventPublisher publisher;
    private final CenterRepository centerRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @PostMapping("/waiting")
    public Long register(@Login Long userId, @RequestBody WaitingRegisterDto dto){
        Long ptDateId = dto.getPtDateId();
        return waitingService.register(userId, ptDateId).getId();
    }

    @DeleteMapping("/waiting")
    public Long cancel(@Login Long userId, @RequestBody WaitingCancelDto dto){
        Long waitingId = dto.getWaitingId();
        return waitingService.cancel(waitingId, userId);
    }

    @GetMapping("/test")
    public String test(){

        Teacher teacher = new Teacher(987654321L, "dddfsd","fdsfd", "ureiui", null, null, null, null, Auth.TEACHER, Approval.ACCEPT, null, null, null);
        userRepository.save(teacher);
        log.info("이벤트 시작");
        //publisher.publishEvent(new ParticipationCancelEvent(ptDate, null)); // 이벤트 리스너 호출
        Center center = centerRepository.findById(1L).get();
        publisher.publishEvent(new AlarmEvent());
        log.info("main 은 끝");
        return "ok";
    }
}
