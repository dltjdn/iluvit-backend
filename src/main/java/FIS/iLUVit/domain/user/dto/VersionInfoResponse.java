package FIS.iLUVit.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VersionInfoResponse {

    private String iosVersion;
    private String aosVersion;
}