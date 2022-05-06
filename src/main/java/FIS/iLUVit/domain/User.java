package FIS.iLUVit.domain;

import javax.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@DiscriminatorValue("null")
public class User extends BaseEntity{
    @GeneratedValue @Id
    private Long id;
    private String nickName;            // 닉네임
    private String loginId;             // 로그인 할때 입력할 아이디
    private String password;            // 비밀번호
    private String phoneNumber;
    private Boolean hasProfileImg;      // 프사 있니?
    private String emailAddress;
    private String name;                // 잔짜 이름

    @OneToMany(mappedBy = "receiver")
    private List<Chat> receiveChats;

    @OneToMany(mappedBy = "sender")
    private List<Chat> sendChats;

}
