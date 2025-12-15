package com.architecture.gestion_de_cvs.repository;

import com.architecture.gestion_de_cvs.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    
    Optional<Invitation> findByToken(String token);
    
    Optional<Invitation> findByEmailAndUsedFalse(String email);
    
    @Query("SELECT i FROM Invitation i WHERE i.token = :token AND i.used = false AND i.expiresAt > :now")
    Optional<Invitation> findValidInvitation(@Param("token") String token, @Param("now") LocalDateTime now);
    
    boolean existsByEmailAndUsedFalse(String email);
}
