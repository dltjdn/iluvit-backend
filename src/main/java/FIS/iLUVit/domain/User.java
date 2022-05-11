package FIS.iLUVit.domain;

import FIS.iLUVit.domain.enumtype.Auth;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@DiscriminatorValue("null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity{
    @GeneratedValue @Id
    protected Long id;
    protected String nickName;            // 닉네임
    protected String loginId;             // 로그인 할때 입력할 아이디
    protected String password;            // 비밀번호
    protected String phoneNumber;
    protected Boolean hasProfileImg;      // 프사 있니?
    protected String emailAddress;
    protected String name;                // 잔짜 이름

    @Enumerated(EnumType.STRING)
    protected Auth auth;                   // 교사, 원장, 부모

    @Column(name = "dtype", insertable = false, updatable = false)
    protected String dtype;               // Teacher or Parent

    @OneToMany(mappedBy = "receiver")
    protected List<Chat> receiveChats;

    @OneToMany(mappedBy = "sender")
    protected List<Chat> sendChats;

    public List<String> getAuthList() {
        if (this.auth != null) {
            return Arrays.asList(this.auth.toString().split(","));
        }
        return new ArrayList<>();
    }



}
