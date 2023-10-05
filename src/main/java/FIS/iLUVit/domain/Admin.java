package FIS.iLUVit.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import FIS.iLUVit.security.AdminDto;
import FIS.iLUVit.domain.enumtype.AdminAuthority;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "CallUser")
public class Admin {

	@Id
	@GeneratedValue
	@Column(name = "user_id")
	private Long id;
	//user_id         BIGINT              // 'primary_key',

	@NotBlank
	@Column(name = "u_nickname", unique = true, length = 100)
	private String nickname;              // "사용자 id"

	@NotBlank
	@Column(name = "u_name", length = 10)
	private String name;                  // '사용자 이름',

	@NotBlank
	@Column(name = "u_pwd")
	private String pwd;                   // '사용자 비밀번호',

	@Column(name = "u_ph", length = 15)
	private String ph;                    // '사용자 전화번호',

	@Column(name = "u_sDate")
	private LocalDate sDate;                 // '입사일'

	@NotNull // enum 때문에 notblank 안됨
	@Column(name = "u_auth")
	@Enumerated(EnumType.STRING)
	private AdminAuthority auth;                  // '권한'

    /* jblee
	@OneToMany(mappedBy = "user")
	private List<Call> callList = new ArrayList<Call>();

	@OneToMany(mappedBy = "user")
	private List<Messenger> messengerList = new ArrayList<Messenger>();
    */

    /*
            날짜 : 2022/01/10 2:58 오후
            작성자 : 원보라
            작성내용 : 생성 메서드
    */

	public Admin(String u_nickname, String u_name, String u_pwd, String u_ph, LocalDate u_sDate, AdminAuthority u_auth) {
		this.nickname = u_nickname;
		this.name = u_name;
		this.pwd = u_pwd;
		this.ph = u_ph;
		this.sDate = u_sDate;
		this.auth = u_auth;
	}

    public AdminDto getLoginInfo() {
        return new AdminDto(this);
    }
}
