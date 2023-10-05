package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Admin;
import FIS.iLUVit.domain.enumtype.AdminAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByNickname(String loginId);

    Optional<Admin> findByPh(String phoneNumber);

    //Optional<Admin> findByIdAndAuth(Long userId, AdminStatus adminStatus);

    List<Admin> findByAuth(AdminAuthority adminStatus);

}
