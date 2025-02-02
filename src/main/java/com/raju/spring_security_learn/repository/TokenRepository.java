package com.raju.spring_security_learn.repository;


import com.raju.spring_security_learn.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

    @Query("""
        SELECT t FROM Token t
        inner join User u
        WHERE t.user.id = :userId AND t.logout = false
        """)

//    @Query("""
//        SELECT t FROM Token t
//        WHERE t.user.id = :userId AND t.logout = false
//        """)
    List<Token> findAllTokenByUser(Long userId);
}
