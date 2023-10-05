package FIS.iLUVit.security;

import FIS.iLUVit.domain.Admin;
import FIS.iLUVit.domain.enumtype.AdminAuthority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {
    private Long id;
    private String nickname;
    private String name;
    private String phoneNumber;
    private AdminAuthority auth;
    private String accessToken;
    private String refreshToken;

    public AdminDto(Admin admin) {
        this.id = admin.getId();
        this.nickname = admin.getNickname();
        this.name = admin.getName();
        this.phoneNumber = admin.getPh();
        this.auth = admin.getAuth();
    }
}
