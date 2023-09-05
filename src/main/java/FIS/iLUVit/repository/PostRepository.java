package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    /**
     * 해당 유저의 게시물 리스트를 조회한다
     */
    Slice<Post> findByUser(User user, Pageable pageable);

    /**
     * 해당 유저로 게시글을 조회합니다
     */
    List<Post> findByUser(User user);

    /**
     * 해당 게시판들을 포함하는 게시물들을 조회한다
     */
    List<Post> findByBoardIn(List<Board> boards);
}
