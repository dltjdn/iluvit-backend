package FIS.iLUVit.security.uesrdetails;

import FIS.iLUVit.domain.Admin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class AdminDetails implements UserDetails {

    private final Admin admin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> ("ROLE_" + admin.getAuth().toString()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return admin.getPwd();
    }

    @Override
    public String getUsername() {
        return admin.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminDetails that = (AdminDetails) o;
        return Objects.equals(admin, that.admin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(admin);
    }
}
