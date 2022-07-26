package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.*;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.ScrapErrorResult;
import FIS.iLUVit.exception.ScrapException;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.ScrapPostRepository;
import FIS.iLUVit.repository.ScrapRepository;
import FIS.iLUVit.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static FIS.iLUVit.controller.dto.UpdateScrapByPostRequest.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScrapServiceTest {

    @InjectMocks
    private ScrapService target;

    @Mock
    private ScrapRepository scrapRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ScrapPostRepository scrapPostRepository;

    private Parent parent1;
    private Parent parent2;
    private Parent parent3;
    private Post post1;
    private Post post2;
    private Post post3;
    private Scrap scrap1;
    private Scrap scrap2;
    private Scrap scrap3;
    private ScrapPost scrapPost1;
    private ScrapPost scrapPost2;
    private Board board1;
    private Board board2;
    private Board board3;
    private Center center1;
    private Center center2;

    @BeforeEach
    public void init() {
        parent1 = Creator.createParent(1L, "parent1", "parent1", "parent1");
        parent2 = Creator.createParent(2L, "parent2", "parent2", "parent2");
        parent3 = Creator.createParent(3L, "parent3", "parent3", "parent3");
        center1 = Center.builder().name("center1").id(12L).build();
        center2 = Center.builder().name("center2").id(13L).build();
        board1 = Creator.createBoard(14L, "board1", null, true);
        board2 = Creator.createBoard(15L, "board2", center1, true);
        board3 = Creator.createBoard(16L, "board3", center2, false);
        post1 = Creator.createPost(4L, "post1", "post1", true, board1, parent1);
        post2 = Creator.createPost(5L, "post2", "post2", true, board2, parent1);
        post3 = Creator.createPost(6L, "post2", "post2", true, board3, parent2);
        scrap1 = Creator.createDefaultScrap(7L, parent1, "scrap1");
        scrap2 = Creator.createScrap(8L, parent1, "scrap2");
        scrap3 = Creator.createScrap(11L, parent2, "scrap3");
        scrapPost1 = Creator.createScrapPost(9L, post1, scrap1);
        scrapPost2 = Creator.createScrapPost(10L, post3, scrap1);
    }

    @Test
    public void 스크랩폴더목록가져오기_성공() {
        // given
        scrap1.getScrapPosts().add(scrapPost1);
        scrap1.getScrapPosts().add(scrapPost2);
        doReturn(Arrays.asList(scrap1, scrap2))
                .when(scrapRepository)
                .findScrapsByUserWithScrapPosts(parent1.getId());
        // when
        ScrapListInfoResponse result = target.findScrapDirListInfo(parent1.getId());
        // then
        assertThat(result.getData().size()).isEqualTo(2);
        result.getData().forEach(scrapInfo -> {
            if (Objects.equals(scrapInfo.getScrapId(), scrap1.getId())) {
                assertThat(scrapInfo.getPostsNum()).isEqualTo(2);
            }
        });
    }

    @Test
    public void 스크랩폴더추가하기_성공() {
        // given
        doReturn(parent1)
                .when(userRepository)
                .getById(parent1.getId());
        scrap1.getScrapPosts().add(scrapPost1);
        scrap1.getScrapPosts().add(scrapPost2);
        Scrap scrap = Creator.createScrap(-1L, parent1, "scrap");
        doReturn(List.of(scrap1, scrap2, scrap))
                .when(scrapRepository)
                .findScrapsByUserWithScrapPosts(parent1.getId());
        AddScrapRequest request = new AddScrapRequest("scrap");
        // when
        ScrapListInfoResponse result = target.addScrapDir(parent1.getId(), request);
        // then
        assertThat(result.getData().size()).isEqualTo(3);
    }

    @Nested
    @DisplayName("스크랩 폴더 삭제하기")
    class deleteScrapDir {
        @Test
        @DisplayName("[error] 잘못된 scrapId")
        public void 잘못된scrapId() {
            // given
            doReturn(Optional.empty())
                    .when(scrapRepository)
                    .findScrapByIdAndUserId(any(), any());
            // when
            ScrapException result = assertThrows(ScrapException.class,
                    () -> target.deleteScrapDir(parent1.getId(), -1L));
            // then
            assertThat(result.getErrorResult()).isEqualTo(ScrapErrorResult.NOT_VALID_SCRAP);
        }
        @Test
        @DisplayName("[error] default 스크랩폴더 삭제시도")
        public void 스크랩폴더삭제() {
            // given
            doReturn(Optional.of(scrap1))
                    .when(scrapRepository)
                    .findScrapByIdAndUserId(scrap1.getId(), parent1.getId());
            // when
            ScrapException result = assertThrows(ScrapException.class,
                    () -> target.deleteScrapDir(parent1.getId(), scrap1.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(ScrapErrorResult.CANT_DELETE_DEFAULT);
        }

        @Test
        @DisplayName("[success] 스크랩폴더삭제성공")
        public void 스크랩폴더삭제성공() {
            // given
            doReturn(Optional.of(scrap2))
                    .when(scrapRepository)
                    .findScrapByIdAndUserId(scrap2.getId(), parent1.getId());
            doReturn(List.of(scrap1))
                    .when(scrapRepository)
                    .findScrapsByUserWithScrapPosts(parent1.getId());
            // when
            ScrapListInfoResponse result = target.deleteScrapDir(parent1.getId(), scrap2.getId());
            // then
            assertThat(result.getData().size()).isEqualTo(1);
            // verify
            verify(scrapRepository, times(1)).delete(scrap2);
        }
    }

    @Test
    public void 스크랩폴더이름바꾸기_성공() {
        // given
        String name = "바뀌는스크랩폴더이름";
        UpdateScrapDirNameRequest request = new UpdateScrapDirNameRequest(scrap1.getId(), name);
        doReturn(Optional.of(scrap1))
                .when(scrapRepository)
                .findScrapByIdAndUserId(any(), any());
        // when
        Scrap result = target.updateScrapDirName(parent1.getId(), request);
        // then
        assertThat(result.getId()).isEqualTo(request.getScrapId());
        assertThat(result.getName()).isEqualTo(name);
    }

    @Nested
    @DisplayName("게시물 스크랩하기")
    class scrapPost{

        @Test
        @DisplayName("[error] db와 요청이 일치하지않음")
        public void db와요청다름() {
            // given
            ScrapInfoForUpdate info1 = new ScrapInfoForUpdate(scrap1.getId(), true);
            ScrapInfoForUpdate info2 = new ScrapInfoForUpdate(scrap2.getId(), false);
            ScrapInfoForUpdate info3 = new ScrapInfoForUpdate(scrap3.getId(), false);
            UpdateScrapByPostRequest request = new UpdateScrapByPostRequest(post1.getId(), List.of(info1, info2, info3));
            doReturn(List.of(scrap1, scrap2))
                    .when(scrapRepository)
                    .findScrapsByUserWithScrapPosts(any());
            doReturn(Optional.of(post1))
                    .when(postRepository)
                    .findById(request.getPostId());
            // when
            ScrapException result = assertThrows(ScrapException.class,
                    () -> target.scrapPost(parent1.getId(), request));

            // then
            assertThat(result.getErrorResult()).isEqualTo(ScrapErrorResult.NOT_VALID_SCRAP);
        }

        @Test
        @DisplayName("[error] 잘못된 postId")
        public void 잘못된게시물아이디() {
            // given
            ScrapInfoForUpdate info1 = new ScrapInfoForUpdate(scrap1.getId(), true);
            ScrapInfoForUpdate info2 = new ScrapInfoForUpdate(scrap2.getId(), true);
            UpdateScrapByPostRequest request = new UpdateScrapByPostRequest(post1.getId(), List.of(info1, info2));
            scrap1.getScrapPosts().add(scrapPost1);
            scrap1.getScrapPosts().add(scrapPost2);
            doReturn(List.of(scrap1, scrap2))
                    .when(scrapRepository)
                    .findScrapsByUserWithScrapPosts(parent1.getId());
            doReturn(Optional.empty())
                    .when(postRepository)
                    .findById(request.getPostId());
            // when
            ScrapException result = assertThrows(ScrapException.class,
                    () -> target.scrapPost(parent1.getId(), request));
            // then
            assertThat(result.getErrorResult()).isEqualTo(ScrapErrorResult.NOT_VALID_POST);
        }

        @Test
        @DisplayName("[success] 새로 스크랩을 해야되는경우")
        public void 새로운스크랩() {
            // given
            ScrapInfoForUpdate info1 = new ScrapInfoForUpdate(scrap1.getId(), true);
            ScrapInfoForUpdate info2 = new ScrapInfoForUpdate(scrap2.getId(), true);
            UpdateScrapByPostRequest request = new UpdateScrapByPostRequest(post1.getId(), List.of(info1, info2));
            scrap1.getScrapPosts().add(scrapPost1);
            scrap1.getScrapPosts().add(scrapPost2);
            doReturn(List.of(scrap1, scrap2))
                    .when(scrapRepository)
                    .findScrapsByUserWithScrapPosts(parent1.getId());
            doReturn(Optional.of(post1))
                    .when(postRepository)
                    .findById(request.getPostId());
            // when
            List<Scrap> result = target.scrapPost(parent1.getId(), request);
            // verify
            verify(scrapPostRepository, times(1)).save(any());
            verify(scrapRepository, times(0)).delete(any());
        }

        @Test
        @DisplayName("[success] 기존스크랩을 취소하는경우")
        public void 기존스크랩취소() {
            // given
            ScrapInfoForUpdate info1 = new ScrapInfoForUpdate(scrap1.getId(), false);
            ScrapInfoForUpdate info2 = new ScrapInfoForUpdate(scrap2.getId(), false);
            UpdateScrapByPostRequest request = new UpdateScrapByPostRequest(post1.getId(), List.of(info1, info2));
            scrap1.getScrapPosts().add(scrapPost1);
            scrap1.getScrapPosts().add(scrapPost2);
            doReturn(List.of(scrap1, scrap2))
                    .when(scrapRepository)
                    .findScrapsByUserWithScrapPosts(parent1.getId());
            doReturn(Optional.of(post1))
                    .when(postRepository)
                    .findById(request.getPostId());
            // when
            target.scrapPost(parent1.getId(), request);
            // verify
            verify(scrapPostRepository, times(0)).save(any());
            verify(scrapPostRepository, times(1)).delete(any());
        }

        @Test
        @DisplayName("[success] 스크랩 등록과 취소를 동시에")
        public void 취소및스크랩동시발생() {
            // given
            ScrapInfoForUpdate info1 = new ScrapInfoForUpdate(scrap1.getId(), false);
            ScrapInfoForUpdate info2 = new ScrapInfoForUpdate(scrap2.getId(), true);
            UpdateScrapByPostRequest request = new UpdateScrapByPostRequest(post1.getId(), List.of(info1, info2));
            scrap1.getScrapPosts().add(scrapPost1);
            scrap1.getScrapPosts().add(scrapPost2);
            doReturn(List.of(scrap1, scrap2))
                    .when(scrapRepository)
                    .findScrapsByUserWithScrapPosts(parent1.getId());
            doReturn(Optional.of(post1))
                    .when(postRepository)
                    .findById(request.getPostId());
            // when
            target.scrapPost(parent1.getId(), request);
            // then
            verify(scrapPostRepository, times(1)).save(any());
            verify(scrapPostRepository, times(1)).delete(any());
        }
    }

    @Nested
    @DisplayName("스크랩한 게시물 스크랩폴더에서 삭제")
    class 스크랩폴더에서스크랩삭제{

        @Test
        @DisplayName("[error] 유호하지않은 scrapPostId")
        public void 유효하지않은스크랩포스트아이디() {
            // given
            doReturn(Optional.empty())
                    .when(scrapPostRepository)
                    .findByScrapAndPost(any(), any());
            // when
            ScrapException result = assertThrows(ScrapException.class,
                    () -> target.deleteScrapPost(parent1.getId(), scrapPost1.getId()));
            // then
            assertThat(result.getErrorResult()).isEqualTo(ScrapErrorResult.NOT_VALID_SCRAPPOST);
        }

        @Test
        @DisplayName("[success] 정상적인 스크랩포스트제거")
        public void 스크랩포스트삭제() {
            // given
            doReturn(Optional.of(scrapPost1))
                    .when(scrapPostRepository)
                    .findByScrapAndPost(any(), any());
            // when
            target.deleteScrapPost(parent1.getId(), scrapPost1.getId());
            // then
            verify(scrapPostRepository, times(1)).delete(scrapPost1);
        }
    }

    @Test
    public void 게시물에대한스크랩상태목록조회() {
        // given
        scrap1.getScrapPosts().add(scrapPost1);
        scrap1.getScrapPosts().add(scrapPost2);
        doReturn(List.of(scrap1, scrap2))
                .when(scrapRepository)
                .findScrapsByUserWithScrapPosts(any());
        // when
        ScrapListByPostResponse result = target.findScrapListByPost(parent1.getId(), post1.getId());
        // then
        assertThat(result.getData().size()).isEqualTo(2);
        result.getData().forEach(scrapInfo -> {
            if (Objects.equals(scrapInfo.getScrapId(), scrap1.getId())) {
                assertThat(scrapInfo.getHasPost()).isTrue();
            } else {
                assertThat(scrapInfo.getHasPost()).isFalse();
            }
        });
    }

    @Test
    public void 스크랩게시물_preview_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        doReturn(new SliceImpl<>(List.of(scrapPost1, scrapPost2), pageable, false))
                .when(scrapPostRepository)
                .findByScrapWithPost(parent1.getId(), scrap1.getId(), PageRequest.of(0, 5));

        // when
        Slice<GetScrapPostResponsePreview> result =
                target.searchByScrap(parent1.getId(), scrap1.getId(), PageRequest.of(0, 5));
        // then
        assertThat(result.getContent().size()).isEqualTo(2);
        result.getContent().forEach(sp -> {
            if (Objects.equals(sp.getPost_id(), post1.getId())) {
                assertThat(sp.getUser_id()).isEqualTo(parent1.getId());
                assertThat(sp.getBoardName()).isEqualTo(board1.getName());
                assertThat(sp.getCenter_id()).isNull();
            } else if (Objects.equals(sp.getPost_id(), post3.getId())) {
                assertThat(sp.getUser_id()).isEqualTo(parent2.getId());
                assertThat(sp.getBoardName()).isEqualTo(board3.getName());
                assertThat(sp.getCenter_id()).isEqualTo(center2.getId());
            }
        });
    }

}
