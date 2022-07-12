package FIS.iLUVit.service.createmethod;

import FIS.iLUVit.domain.Board;
import FIS.iLUVit.domain.Bookmark;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.BoardKind;

public class CreateTest {

    public static Board createBoard(Long id, String name, BoardKind boardKind, Center center, Boolean isDefault) {
        return Board.builder()
                .id(id)
                .name(name)
                .boardKind(boardKind)
                .center(center)
                .isDefault(isDefault)
                .build();
    }

    public static Board createBoard(String name, BoardKind boardKind, Center center, Boolean isDefault) {
        return Board.builder()
                .name(name)
                .boardKind(boardKind)
                .center(center)
                .isDefault(isDefault)
                .build();
    }

    public static Center createCenter(Long id, String name) {
        return Center.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static Center createCenter(String name) {
        return Center.builder()
                .name(name)
                .build();
    }

    public static Bookmark createBookmark(Long id, Board board, User user) {
        return Bookmark.builder()
                .id(id)
                .board(board)
                .user(user)
                .build();
    }

    public static Bookmark createBookmark(Board board, User user) {
        return Bookmark.builder()
                .board(board)
                .user(user)
                .build();
    }
}
