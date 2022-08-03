package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.BoardListDTO;
import FIS.iLUVit.controller.dto.CreateBoardRequest;
import FIS.iLUVit.controller.dto.StoryHomeDTO;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final BookmarkRepository bookmarkRepository;
    private final CenterRepository centerRepository;
    private final UserRepository userRepository;
    private final ChildRepository childRepository;

    public BoardListDTO findAllWithBookmark(Long userId) {
        BoardListDTO dto = new BoardListDTO(null, "모두의 이야기");
        // 모두의 이야기 내 유저의 북마크 정보
        List<Bookmark> bookmarks = bookmarkRepository.findBoardByUser(userId);
        // 모두의 이야기 내 모든 게시판
        List<Board> boards = boardRepository.findByCenterIsNull();
        // DTO 생성 후 반환
        createDTO(bookmarks, boards, dto);

        return dto;
    }

    public BoardListDTO findAllWithBookmarkInCenter(Long userId, Long centerId) {
        Center findCenter = centerRepository.findById(centerId)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));
        BoardListDTO dto = new BoardListDTO(centerId, findCenter.getName());
        // 시설(유치원)의 이야기 내 유저의 북마크 정보
        List<Bookmark> bookmarks = bookmarkRepository.findBoardByUserAndCenter(userId, centerId);
        // 시설(유치원)의 이야기 모든 게시판
        List<Board> boards = boardRepository.findByCenter(centerId);
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

    public Long create(Long userId, Long center_id, CreateBoardRequest request) {
        // userId 가 null 인 경우 게시판 생성 제한
        if (userId == null) {
            throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        // 모두의 이야기에서 게시판 이름 중복성 검사 및 저장
        if (center_id == null) {
            boardRepository.findByName(request.getBoard_name())
                    .ifPresent((b) -> {
                        throw new BoardException(BoardErrorResult.BOARD_NAME_DUPLICATION);
                    });
            return boardRepository.save(Board.createBoard(
                    request.getBoard_name(), request.getBoardKind(), null, false)).getId();
        }

        // 센터가 존재하는 지 검사
        Center findCenter = centerRepository.findById(center_id)
                .orElseThrow(() -> new CenterException(CenterErrorResult.CENTER_NOT_EXIST));

        // 시설의 이야기에서 센터에 속하지 않은 회원은 게시판 생성 불가
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("유저 아이디 오류"));

        if (findUser.getAuth() == Auth.PARENT) {
            childRepository.findByParentAndCenter(userId, center_id)
                    .orElseThrow(() -> new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS));
        } else {
            Teacher teacher = (Teacher) findUser;
            if (teacher.getCenter() == null || teacher.getCenter().getId() != center_id) {
                throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
            }
        }

        // 시설의 이야기에서 게시판 이름 중복성 검사 및 저장
        boardRepository.findByNameWithCenter(request.getBoard_name(), center_id)
                .ifPresent((b) -> {
                    throw new BoardException(BoardErrorResult.BOARD_NAME_DUPLICATION);
                });


        Board board = Board.createBoard(request.getBoard_name(), request.getBoardKind(), findCenter,false);
        Board savedBoard = boardRepository.save(board);
        return savedBoard.getId();
    }

    public Long remove(Long userId, Long boardId) {
        // userId 가 null 인 경우 게시판 삭제 제한
        if (userId == null) {
            throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
        }

        // board id 오류
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardException(BoardErrorResult.BOARD_NOT_EXIST));

        /**
         * 1. 학부모 -> 삭제 불가
         * 2. DIRECTOR 권한X -> 삭제 불가
         * 3. 센터 값X -> 삭제 불가
         * 4. 원장이 속한 센터 != 게시판이 속한 센터 -> 삭제 불가
         * 5. 디폴트 게시판 -> 삭제 불가
         */
        userRepository.findById(userId)
                .ifPresent(u -> {
                    if (u.getAuth() == Auth.PARENT) {
                        throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
                    } else {
                        Teacher t = (Teacher) u;
                        if (t.getAuth() != Auth.DIRECTOR) {
                            throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
                        }
                        if (t.getCenter() == null) {
                            throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
                        }
                        if (t.getCenter().getId() != findBoard.getCenter().getId()) {
                            throw new BoardException(BoardErrorResult.UNAUTHORIZED_USER_ACCESS);
                        }
                    }
                });

        if (findBoard.getIsDefault()) {
            throw new BoardException(BoardErrorResult.DEFAULT_BOARD_DELETE_BAN);
        }

        boardRepository.delete(findBoard);
        return boardId;
    }

    public StoryHomeDTO findCenterStory(Long userId) {
        List<StoryHomeDTO.CenterStoryDTO> result = new ArrayList<>();
        result.add(new StoryHomeDTO.CenterStoryDTO(null));
        if (userId == null) {
            return new StoryHomeDTO(result);
        }
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_EXIST));
        log.info("findUser = {}", findUser.getAuth());
        if (findUser.getAuth() == Auth.PARENT) {
            List<Child> children = userRepository.findChildrenWithCenter(userId);
            List<StoryHomeDTO.CenterStoryDTO> centerStoryDTOList = children.stream()
                    .filter(c -> c.getCenter() != null && c.getApproval() == Approval.ACCEPT)
                    .map(c -> new StoryHomeDTO.CenterStoryDTO(c.getCenter()))
                    .collect(Collectors.toList());
            result.addAll(centerStoryDTOList);
        } else {
            Center findCenter = ((Teacher) findUser).getCenter();
            Approval approval = ((Teacher) findUser).getApproval();
            if (findCenter != null && approval == Approval.ACCEPT) {
                StoryHomeDTO.CenterStoryDTO centerStoryDTO = new StoryHomeDTO
                        .CenterStoryDTO(findCenter);
                result.add(centerStoryDTO);
            }
        }
        return new StoryHomeDTO(result);
    }
}
