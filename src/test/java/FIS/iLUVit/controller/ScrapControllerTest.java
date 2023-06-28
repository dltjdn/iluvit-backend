package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.scrap.ScrapDirRequest;
import FIS.iLUVit.dto.scrap.ScrapDirDetailRequest;
import FIS.iLUVit.dto.scrap.ScrapDirUpdateRequest;
import FIS.iLUVit.dto.scrap.ScrapInfoDto;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.exception.ScrapErrorResult;
import FIS.iLUVit.exception.ScrapException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.ScrapService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ScrapControllerTest {

    // TODO 스크랩폴더목록정보가져오기_성공










    @Nested
    @DisplayName("스크랩폴더추가하기")
    class addScrap{

        // TODO 스크랩폴더추가하기_성공








        // TODO 스크랩폴더추가하기_실패_불완전한요청


    }

    @Nested
    @DisplayName("스크랩 폴더 삭제하기")
    class deleteScrapDir{

        // TODO 잘못된스크랩아이디








        // TODO 스크랩폴더삭제성공



    }
    @Nested
    @DisplayName("스크랩폴더이름바꾸기")
    class changeScrapName{

        // TODO 불완전한요청








        // TODO 잘못된스크랩아이디








        // TODO 이름바꾸기성공



    }

    @Nested
    @DisplayName("게시물 스크랩하기")
    class scrapPost{

        // TODO 불완전한요청








        // TODO 게시물아이디 오류








        // TODO 스크랩아이디 오류








        // TODO 게시물스크랩성공


    }

    @Nested
    @DisplayName("스크랩취소")
    class deleteScrapPost{

        // TODO 잘못된 scrapPost








        // TODO 스크랩취소성공


    }


    // TODO 게시물에대한스크랩목록조회_성공








    // TODO 스크램게시물프리뷰_성공



}