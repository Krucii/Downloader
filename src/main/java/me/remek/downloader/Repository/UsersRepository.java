package me.remek.downloader.Repository;

import me.remek.downloader.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Users getUsersById(Long id);
}
