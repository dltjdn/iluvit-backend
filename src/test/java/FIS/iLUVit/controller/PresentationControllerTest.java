package FIS.iLUVit.controller;

import FIS.iLUVit.config.argumentResolver.LoginUserArgumentResolver;
import FIS.iLUVit.dto.presentation.PresentationDetailRequest;
import FIS.iLUVit.dto.presentation.PtDateDetailRequest;
import FIS.iLUVit.dto.presentation.PresentationResponse;
import FIS.iLUVit.domain.Parent;
import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.domain.PtDate;
import FIS.iLUVit.domain.User;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.exception.PresentationErrorResult;
import FIS.iLUVit.exception.PresentationException;
import FIS.iLUVit.exception.UserErrorResult;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.exception.exceptionHandler.ErrorResponse;
import FIS.iLUVit.exception.exceptionHandler.controllerAdvice.GlobalControllerAdvice;
import FIS.iLUVit.service.PresentationService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static FIS.iLUVit.Creator.createPtDate;
import static FIS.iLUVit.Creator.createValidPresentation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PresentationControllerTest {

    @Nested
    @DisplayName("설명회 저장")
    class 설명회저장{

        // TODO 로그인을 하지 않은 경우










        // TODO 잘못된 요청











        // TODO 존재 하지 않는 선생님










        // TODO 이미 유효한 설명회 존재










        // TODO 정보저장성공_APP용 (설명회 정보 저장 성공)










        // TODO 이미지저장성공_APP용 (설명회 이미지 저장 성공)


    }

}