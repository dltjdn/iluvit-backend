package FIS.iLUVit.controller.dto;

import lombok.Data;

@Data
public class AuthenticateAuthNumRequest {
    String phoneNum;
    String authNum;
}
