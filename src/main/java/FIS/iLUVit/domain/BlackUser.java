package FIS.iLUVit.domain;

import FIS.iLUVit.domain.embeddable.Location;
import FIS.iLUVit.domain.enumtype.Auth;
import FIS.iLUVit.domain.enumtype.UserStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "black_user")
public class BlackUser extends BaseImageEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private Long userId;              // 사용자 기본키
    private String nickName;          // 닉네임
    private String loginId;           // 로그인 아이디
    private String password;          // 비밀번호
    private String phoneNumber;       // 핸드폰 번호
    private String emailAddress;      // 이메일
    private String name;              // 이름
    private String address;           // 주소
    private String detailAddress;     // 상세주소
    private Boolean readAlarm;        // 알림 모두 읽음 or 안 읽은 알림 있음
    @Embedded
    private Location location;
    @Enumerated(EnumType.STRING)
    private Auth auth;// Teacher, Director or Parent
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;      // Suspended, Restricted or Withdrawn

    public BlackUser(User user, UserStatus userStatus) {
        this.userId = user.getId();
        this.nickName = user.getNickName();
        this.loginId = user.getLoginId();
        this.password = user.getPassword();
        this.phoneNumber = user.getPhoneNumber();
        this.emailAddress = user.getEmailAddress();
        this.name = user.getName();
        this.address = user.getAddress();
        this.detailAddress = user.getDetailAddress();
        this.readAlarm = user.getReadAlarm();
        this.location = user.getLocation();
        this.auth = user.getAuth();
        this.profileImagePath = user.getProfileImagePath();
        this.infoImagePath = user.getInfoImagePath();
        this.imgCnt = user.getImgCnt();
        this.userStatus = userStatus;
    }
}