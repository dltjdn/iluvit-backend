package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.board.BoardPreviewDto;
import FIS.iLUVit.dto.post.PostPreviewDto;
import FIS.iLUVit.dto.post.PostRequest;
import FIS.iLUVit.dto.post.PostResponse;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.PostService;
import FIS.iLUVit.service.createmethod.CreateTest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    // TODO 게시글_저장_비회원










    // TODO 게시글_저장_유저X










    // TODO 게시글_저장_게시판X











    // TODO 게시글_저장_공지게시판에_학부모가_작성시도










    // TODO 게시글_저장_성공











    // TODO 게시글_저장_성공_APP용











    // TODO 게시글_삭제_비회원











    // TODO 게시글_삭제_유저X











    // TODO 게시글_삭제_게시글X












    // TODO 게시글_삭제_권한X













    // TODO 게시글_삭제_성공













    // TODO 게시글_1개_조회_게시글X













    // TODO 게시글_1개_조회_성공













    // TODO 게시글_제목_내용_검색_비회원











    // TODO 게시글_제목_내용_검색_유저X











    // TODO 게시글_제목_내용_검색_성공












    // TODO 센터에서_게시글_제목_내용_검색_해당_센터에_권한없음












    // TODO 보드에서_게시글_제목_내용_검색












    // TODO HOT_게시판_글_목록_조회












    // TODO 내가_쓴_글_리스트












    // TODO 모두의_이야기_글_리스트_불러오기_비회원












    // TODO 모두의_이야기_글_리스트_불러오기_회원











    // TODO 유치원별_이야기_글_리스트_불러오기_비회원











    // TODO 유치원별_이야기_글_리스트_불러오기_유저X











    // TODO 유치원별_이야기_글_리스트_불러오기_권한X












    // TODO 유치원별_이야기_글_리스트_불러오기_성공












    // TODO 장터글_끌어올리기_비회원












    // TODO 장터글_끌어올리기_게시글X













    // TODO 장터글_끌어올리기_권한X












    // TODO 장터글_끌어올리기_성공


}