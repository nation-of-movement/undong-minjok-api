package com.undongminjok.api.user.repository;

import com.undongminjok.api.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByLoginId(String loginId);

  Boolean existsByLoginId(String loginId);

  Optional<User> findByEmail(String email);
}
