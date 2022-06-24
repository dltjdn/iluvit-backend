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
        List<Bookmark> bookmarks = bookmarkRepository.findBoardByUser(userId);
        List<Board> boards = boardRepository.findByUserWithCenterIsNull();

        createDTO(bookmarks, boards, dto);

        return dto;
    }

    public BoardListDTO findAllWithBookmarkInCenter(Long userId, Long centerId) {
        BoardListDTO dto = new BoardListDTO();
        List<Bookmark> bookmarks = bookmarkRepository.findBoardByUserAndCenter(userId, centerId);
        List<Board> boards = boardRepository.findByUserWithCenter(centerId);

        createDTO(bookmarks, boards, dto);

        return dto;
    }

    private void createDTO(List<Bookmark> bookmarks, List<Board> boards, BoardListDTO dto) {
        Map<Long, List<Bookmark>> bookmarkMap = bookmarks.stream()
                .collect(Collectors.groupingBy(b -> b.getBoard().getId()));

        boards.forEach(b -> {
            List<Bookmark> bookmarkList = bookmarkMap.get(b.getId());
            if (bookmarkList == null) {
                dto.getBoardList().add(new BoardListDTO.BookmarkDTO(b));
            } else {
                BoardListDTO.BookmarkDTO bookmarkDTO = new BoardListDTO.BookmarkDTO(b);
                bookmarkDTO.setBookmark_id(bookmarkList.get(0).getId());
                dto.getBookmarkList().add(bookmarkDTO);
            }
        });
    }

    public void create(Long center_id, CreateBoardRequest request) {
        if (center_id == null) {
            boardRepository.findByName(request.getBoard_name())
                    .ifPresent((b) -> {
                        throw new BoardException(b.getName() + " == " + request.getBoard_name() + " : 이름 중복");
                    });
            boardRepository.save(Board.createBoard(request.getBoard_name(), request.getBoardKind(), null));
            return;
        }

        boardRepository.findByNameWithCenter(request.getBoard_name(), center_id)
                .ifPresent((b) -> {
                    throw new BoardException(b.getName() + " == " + request.getBoard_name() + " : 이름 중복");
                });

        Center findCenter = centerRepository.findById(center_id)
                .orElseThrow(() -> new CenterException("존재하지 않는 시설"));


        Board board = Board.createBoard(request.getBoard_name(), request.getBoardKind(), findCenter);
        boardRepository.save(board);
    }
}
