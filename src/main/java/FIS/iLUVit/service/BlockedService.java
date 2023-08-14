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

    private final AlarmRepository alarmRepository;
    private final PostRepository postRepository;

    /**
     * 차단당한 유저와 관련된 알림을 삭제합니다
     */
    private void deleteAlarmForBlockedUser(User blockingUser, User blockedUser) {
        // 차단당한 유저가 작성한 게시글로부터 받은 알림 삭제
        Post post = postRepository.findByUser(blockedUser)
                .orElseThrow(() -> new PostException(PostErrorResult.NO_POST_FOUND));
        alarmRepository.deleteByUserAndPostId(blockingUser, post.getId());

        // 차단당한 유저로부터 받은 쪽지에 대한 알림 삭제
        alarmRepository.deleteByUserAndSenderId(blockingUser, blockedUser.getId());
    }

}
