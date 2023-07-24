package FIS.iLUVit.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoliceLoginRequest {
    private String u_nickname;
    private String u_pwd;
}
