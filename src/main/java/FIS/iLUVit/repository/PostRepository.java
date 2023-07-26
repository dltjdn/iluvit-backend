package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    /**
     * 해당 게시판의 게시물 리스트를 조회한다
     */
    List<Post> findByBoardOrderByIdDesc(Board board);

    /**
     * 해당 유저의 게시물 리스트를 조회한다
     */
    Slice<Post> findByUser(User user, Pageable pageable);

    /**
     * 해당 게시판 아이디들의 게시물들을 정렬하여 앞에서부터 3개를 조회한다
     */
    List<Post> findFirst3ByBoardInOrderByBoardIdAscCreatedDateDesc(List<Board> boards);

    /**
     * 일정 좋아요 개수 이상의 게시물을 조회한다 ( 센터가 null이면 모든 게시물, 센터가 null이 아니면 해당 센터의 게시물 )
     */
    @Query("SELECT p FROM Post p JOIN p.board b " +
            "WHERE (:centerId IS NULL OR b.center.id = :centerId) AND p.heartCnt >= :heartCnt " +
            "ORDER BY p.postCreateDate DESC ")
    List<Post> findHotPostsByHeartCnt(int heartCnt, Long centerId, Pageable pageable);


}
