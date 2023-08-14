package FIS.iLUVit.service;

import FIS.iLUVit.domain.Blocked;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.AlarmRepository;
import FIS.iLUVit.repository.BlockedRepository;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockedService {

    private final BlockedRepository blockedRepository;
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;
    private final PostRepository postRepository;

    /**
     * 차단 관계를 생성합니다
     */
    public void createBlocked(Long blockingUserId, Long blockedUserId) {
        // 차단 관계를 생성할 유저들의 정보 조회
        User blockingUser = userRepository.findById(blockingUserId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        User blockedUser = userRepository
                .findById(blockedUserId).orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));

        // 이미 차단된 경우 예외 발생
        Optional<Blocked> existingBlocked = blockedRepository.findByBlockingUserAndBlockedUser(blockingUser, blockedUser);
        if(existingBlocked.isPresent()) {
            throw new BlockedException(BlockedErrorResult.ALREADY_BLOCKED_EXIST);
        }

        // 차단 정보 생성
        Blocked blocked = Blocked.builder()
                .blockingUser(blockingUser)
                .blockedUser(blockedUser)
                .build();
        // 차단 정보 저장
        blockedRepository.save(blocked);

        // 차단당한 유저와 관련된 알림 삭제
        deleteAlarmForBlockedUser(blockingUser, blockedUser);
    }

    /**
     * 차단당한 유저와 관련된 알림을 삭제합니다
     */
    private void deleteAlarmForBlockedUser(User blockingUser, User blockedUser) {
        // 차단한 유저가 작성한 게시글에 차단당한 유저가 댓글을 작성하여 발행한 알림 삭제
        Post post = postRepository.findByUser(blockingUser)
                .orElseThrow(() -> new PostException(PostErrorResult.NO_POST_FOUND));
        alarmRepository.deleteByBlockedUserAndPostId(blockedUser.getNickName(), post.getId());

        // 차단당한 유저로부터 받은 쪽지에 대한 알림 삭제
        alarmRepository.deleteByUserAndSenderId(blockingUser, blockedUser.getId());
    }

}
