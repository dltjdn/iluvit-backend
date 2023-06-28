package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.chat.ChatDto;
import FIS.iLUVit.dto.chat.ChatListDto;
import FIS.iLUVit.dto.chat.ChatRequest;
import FIS.iLUVit.dto.chat.ChatRoomRequest;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.BoardKind;
import FIS.iLUVit.exception.ChatErrorResult;
import FIS.iLUVit.exception.ChatException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.ChatService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {


    // TODO 쪽지_작성_대화방_생성_비회원_접근














    // TODO 쪽지_작성_대화방_생성_자신에게_보냄














    // TODO 쪽지_작성_대화방_생성_유저X















    // TODO 쪽지_작성_대화방_생성_게시글X















    // TODO 쪽지_작성_대화방_생성_성공















    // TODO 쪽지_작성_대화방_생성_후_비회원_혹은_권한X
















    // TODO 쪽지_작성_대화방_생성_후_채팅방_아이디X
















    // TODO 쪽지_작성_대화방_생성_후_탈퇴한_회원끼리_대화

















    // TODO 쪽지_작성_대화방_생성_후_자신에게_쪽지작성

















    // TODO 쪽지_작성_대화방_생성_후_성공
















    // TODO 나의_쪽지함_조회
















    // TODO 쪽지_자세히_보기
















    // TODO 대화방_삭제_탈퇴한_유저
















    // TODO 대화방_삭제_권한없는_유저
















    // TODO 대화방_삭제_성공



}
