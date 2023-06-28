package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Approval;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.dto.review.*;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.ReviewService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    // TODO 학부모가_작성한_리뷰_조회









    // TODO 리뷰_등록_토큰X









    // TODO 리뷰_등록_유저X









    // TODO 리뷰_등록_권한X









    // TODO 리뷰_등록_센터X









    // TODO 리뷰_등록_같은_센터에_2개_이상









    // TODO 리뷰_등록_성공









    // TODO 리뷰_수정_리뷰X










    // TODO 리뷰_수정_권한X










    // TODO 리뷰_수정_성공










    // TODO 리뷰_삭제_리뷰X










    // TODO 리뷰_삭제_권한X











    // TODO 리뷰_삭제_성공












    // TODO 센터에_올라온_리뷰들_조회











    // TODO 시설_리뷰_답글_등록_수정_리뷰X










    // TODO 시설_리뷰_답글_등록_수정_유저X










    // TODO 시설_리뷰_답글_등록_수정_승인X










    // TODO 시설_리뷰_답글_등록_수정_권한X










    // TODO 시설_리뷰_답글_등록_수정_성공










    // TODO 시설_리뷰_답글_삭제_리뷰X










    // TODO 시설_리뷰_답글_삭제_권한X










    // TODO 시설_리뷰_답글_삭제_성공



}