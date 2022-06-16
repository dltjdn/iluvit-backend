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
import java.util.Objects;

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
    @Column(unique = true)
    protected String loginId;             // 로그인 할때 입력할 아이디
    protected String password;            // 비밀번호
    @Column(unique = true)
    protected String phoneNumber;         // 핸드폰 번호
    protected Boolean hasProfileImg;      // 프사 있니?
    protected String emailAddress;        // 이메일
    protected String name;                // 잔짜 이름

    @Enumerated(EnumType.STRING)
    protected Auth auth;                   // 교사, 원장, 부모

    @Column(name = "dtype", insertable = false, updatable = false)
    protected String dtype;               // Teacher or Parent

    @OneToMany(mappedBy = "receiver")
    protected List<Chat> receiveChats;

    @OneToMany(mappedBy = "sender")
    protected List<Chat> sendChats;


    public void changePassword(String newPwd) {
        this.password = newPwd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
