package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.BoardListDTO;
import FIS.iLUVit.controller.dto.CreateBoardRequest;
import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Bookmark;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.exception.BoardException;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.repository.BoardRepository;
import FIS.iLUVit.repository.BookmarkRepository;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CenterRepository centerRepository;

    public BoardListDTO findAllWithBookmark(Long userId) {
        BoardListDTO dto = new BoardListDTO();
        // 모두의 이야기 내 유저의 북마크 정보
        List<Bookmark> bookmarks = bookmarkRepository.findBoardByUser(userId);
        // 모두의 이야기 내 모든 게시판
        List<Board> boards = boardRepository.findByUserWithCenterIsNull();
        // DTO 생성 후 반환
        createDTO(bookmarks, boards, dto);

        return dto;
    }

    public BoardListDTO findAllWithBookmarkInCenter(Long userId, Long centerId) {
        BoardListDTO dto = new BoardListDTO();
        // 시설(유치원)의 이야기 내 유저의 북마크 정보
        List<Bookmark> bookmarks = bookmarkRepository.findBoardByUserAndCenter(userId, centerId);
        // 시설(유치원)의 이야기 모든 게시판
        List<Board> boards = boardRepository.findByUserWithCenter(centerId);
        // DTO 생성 후 반환
        createDTO(bookmarks, boards, dto);

        return dto;
    }

    private void createDTO(List<Bookmark> bookmarks, List<Board> boards, BoardListDTO dto) {
        // 북마크 정보를 게시판 id 으로 그루핑
        Map<Long, List<Bookmark>> bookmarkMap = bookmarks.stream()
                .collect(Collectors.groupingBy(b -> b.getBoard().getId()));

        // 모두의 이야기 내 모든 게시판에서
        boards.forEach(b -> {
            List<Bookmark> bookmarkList = bookmarkMap.get(b.getId());
            if (bookmarkList == null) { // 즐찾 안한 게시판들은 보드 리스트에 넣음
                dto.getBoardList().add(new BoardListDTO.BookmarkDTO(b));
            } else { // 즐찾한 게시판들은 북마크 리스트에 넣음
                BoardListDTO.BookmarkDTO bookmarkDTO = new BoardListDTO.BookmarkDTO(b);
                bookmarkDTO.setBookmark_id(bookmarkList.get(0).getId());
                dto.getBookmarkList().add(bookmarkDTO);
            }
        });
    }

    public Long create(Long center_id, CreateBoardRequest request) {
        // 센터 아이디가 null 인 모두의 이야기에서 게시판 이름 중복성 검사 및 저장
        if (center_id == null) {
            boardRepository.findByName(request.getBoard_name())
                    .ifPresent((b) -> {
                        throw new BoardException(b.getName() + " == " + request.getBoard_name() + " : 이름 중복");
                    });
            return boardRepository.save(Board.createBoard(
                    request.getBoard_name(), request.getBoardKind(), null, false)).getId();
        }

        // 시설의 이야기에서 게시판 이름 중복성 검사 및 저장
        boardRepository.findByNameWithCenter(request.getBoard_name(), center_id)
                .ifPresent((b) -> {
                    throw new BoardException(b.getName() + " == " + request.getBoard_name() + " : 이름 중복");
                });

        Center findCenter = centerRepository.findById(center_id)
                .orElseThrow(() -> new CenterException("존재하지 않는 시설"));

        Board board = Board.createBoard(request.getBoard_name(), request.getBoardKind(), findCenter,false);
        Board savedBoard = boardRepository.save(board);
        return savedBoard.getId();
    }

    public Long remove(Long userId, Long boardId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException("존재하지 않는 게시판"));

        if (findBoard.getIsDefault()) {
            throw new BoardException("기본 게시판들은 삭제할 수 없습니다.");
        }

        boardRepository.delete(findBoard);
        return boardId;
    }
}
