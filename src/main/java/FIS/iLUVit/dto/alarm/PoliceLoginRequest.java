package FIS.iLUVit.dto.alarm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoliceLoginRequest {
    @JsonProperty("u_nickname")
    private String uNickname;
    @JsonProperty("u_pwd")
    private String uPwd;
}