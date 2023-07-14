package FIS.iLUVit.repository;

import FIS.iLUVit.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardBookmarkRepository extends JpaRepository<Bookmark, Long> {
    /**
     * 센테에 속해있지 않은 게시판 중 해당 유저의 게시판 즐겨찾기 리스트를 조회합니다
     */
    List<Bookmark> findByUserAndBoardCenterIsNull(User user);

    /**
     * 해당 게시판 중 해당 유저의 게시판 즐겨찾기를 조회합니다
     */
    Optional<Bookmark> findByUserAndBoard(User user, Board board);

    /**
     * 해당 센터의 게시판 중 해당 유저의 게시판 즐겨찾기 리스트를 조회합니다
     */
    List<Bookmark> findByUserAndBoardCenter(User user, Center center);

    /**
     * 해당 유저와 해당 게시판들의 게시판 즐겨찾기를 삭제합니다
     */
    void deleteByUserAndBoardIn(User user, List<Board> boards);

    /**
     * 해당 유저의 게시판 즐겨찾기를 조회합니다
     */
    List<Bookmark> findByUser(User user);
}
