package FIS.iLUVit.domain.user.domain;

import FIS.iLUVit.domain.blackuser.domain.BlackUser;
import FIS.iLUVit.domain.common.domain.BaseImageEntity;
import FIS.iLUVit.domain.common.domain.Location;
import FIS.iLUVit.domain.user.dto.UserFindOneResponse;
import FIS.iLUVit.domain.common.domain.Auth;
import FIS.iLUVit.domain.user.dto.UserLoginResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@DiscriminatorValue("null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseImageEntity {

    @GeneratedValue @Id
    protected Long id;

    protected String nickName;            // 닉네임

    @Column(unique = true)
    protected String loginId;             // 로그인 할때 입력할 아이디
    protected String password;            // 비밀번호
    @Column(unique = true)
    protected String phoneNumber;         // 핸드폰 번호
    protected String emailAddress;        // 이메일
    protected String name;                // 잔짜 이름
    protected String address;             // 주소
    protected String detailAddress;       // 상세주소

    @Embedded
    protected Location location;

    protected Boolean readAlarm; // true = 알림 모두 읽음, false = 안 읽은 알림 있음

    @Enumerated(EnumType.STRING)
    protected Auth auth;                   // Teacher or Director or Parent

    @Column(name = "dtype", insertable = false, updatable = false)
    protected String dtype;               // Teacher or Parent


    public void changePassword(String newPwd) {
        this.password = newPwd;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || !(object instanceof User)) return false;
        User user = (User) object;
        return id.equals(user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UserLoginResponse getLoginInfo() {
        return new UserLoginResponse(this);
    }

    public UserFindOneResponse getUserInfo() {
        return new UserFindOneResponse(id, nickName, auth);
    }

    public User updateReadAlarm(Boolean readAlarm) {
        this.readAlarm = readAlarm;
        return this;
    }

    public User updateLocation(Location location) {
        this.location = location;
        return this;
    }

    public void disableTutorial() {
        if (this.createdDate.equals(this.updatedDate)) {
            this.updatedDate = LocalDateTime.now();
        }
    }

    public void deletePersonalInfo(){
        this.nickName = "알 수 없음";
        this.loginId = null;
        this.password = null;
        this.phoneNumber = null;
        this.emailAddress = null;
        this.address = null;
        this.name=null;
        this.detailAddress = null;
        this.profileImagePath = "basic";
        this.infoImagePath = null;
        this.imgCnt = null;
        this.location = null;
        this.auth=null;
    }

    public void restorePersonalInfo(BlackUser blackUser){
        this.nickName = blackUser.getNickName();
        this.loginId = blackUser.getLoginId();
        this.password = blackUser.getPassword();
        this.phoneNumber = blackUser.getPhoneNumber();
        this.emailAddress = blackUser.getEmailAddress();
        this.address = blackUser.getAddress();
        this.name = blackUser.getName() ;
        this.detailAddress = blackUser.getDetailAddress();
        this.profileImagePath = blackUser.getProfileImagePath();
        this.infoImagePath = blackUser.getInfoImagePath();
        this.imgCnt = blackUser.getImgCnt();
        this.location = blackUser.getLocation();
        this.auth = blackUser.getAuth();
    }
}
