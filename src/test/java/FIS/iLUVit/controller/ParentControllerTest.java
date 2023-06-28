package FIS.iLUVit.controller;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.parent.ParentDetailResponse;
import FIS.iLUVit.dto.parent.SignupParentRequest;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.exception.*;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.CenterBookmarkService;
import FIS.iLUVit.service.ParentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ParentControllerTest {

    // TODO 학부모회원가입_실패_비밀번호길이짧음












    // TODO 학부모회원가입_실패_일부필드null














    // TODO 학부모회원가입_실패_비밀번호확인틀림













    // TODO 학부모회원가입_실패_중복아이디닉네임














    // TODO 학부모회원가입_실패_핸드폰미인증













    // TODO 학부모회원가입_실패_인증번호만료














    // TODO 학부모회원가입_성공














    // TODO 학부모프로필조회_성공














    // TODO 학부모프로필수정_실패_불완전한요청













    // TODO 학부모프로필수정_성공



}
