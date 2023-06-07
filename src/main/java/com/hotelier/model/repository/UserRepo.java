package com.hotelier.model.repository;

import com.hotelier.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmailAddress (String emailAddress);
    boolean existsByEmailAddress(String email);

    @Query("SELECT u FROM User u WHERE u.emailAddress LIKE %:searchTerm% OR u.mobile LIKE %:searchTerm% OR CONCAT(u.firstname, ' ', u.lastname) LIKE %:searchTerm%")
    List<User> searchByEmailPhoneAndFullName(@Param("searchTerm") String searchTerm);
}
