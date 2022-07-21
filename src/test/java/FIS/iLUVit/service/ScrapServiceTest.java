package FIS.iLUVit.service;

import FIS.iLUVit.Creator;
import FIS.iLUVit.controller.dto.AddScrapRequest;
import FIS.iLUVit.controller.dto.ScrapListInfoResponse;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Post;
import FIS.iLUVit.domain.Scrap;
import FIS.iLUVit.domain.ScrapPost;
import FIS.iLUVit.repository.PostRepository;
import FIS.iLUVit.repository.ScrapPostRepository;
import FIS.iLUVit.repository.ScrapRepository;
import FIS.iLUVit.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
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
    private ScrapPost scrapPost1;
    private ScrapPost scrapPost2;

    @BeforeEach
    public void init() {
        parent1 = Creator.createParent(1L, "parent1", "parent1", "parent1");
        parent2 = Creator.createParent(2L, "parent2", "parent2", "parent2");
        parent3 = Creator.createParent(3L, "parent3", "parent3", "parent3");
        post1 = Creator.createPost(4L, "post1", "post1", true, null, parent1);
        post2 = Creator.createPost(5L, "post2", "post2", true, null, parent1);
        post3 = Creator.createPost(6L, "post2", "post2", true, null, parent2);
        scrap1 = Creator.createScrap(7L, parent1, "scrap1");
        scrap2 = Creator.createScrap(8L, parent1, "scrap2");
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
                .findScrapsWithScrapPostsByUser(parent1.getId());
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
        doReturn(Arrays.asList(scrap1, scrap2, scrap))
                .when(scrapRepository)
                .findScrapsWithScrapPostsByUser(parent1.getId());
        AddScrapRequest request = new AddScrapRequest("scrap");
        // when
        ScrapListInfoResponse result = target.addScrapDir(parent1.getId(), request);
        // then
        assertThat(result.getData().size()).isEqualTo(3);
    }
}
