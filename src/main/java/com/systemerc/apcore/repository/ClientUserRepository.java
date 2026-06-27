package com.systemerc.apcore.repository;

import com.systemerc.apcore.entity.ClientUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientUserRepository extends JpaRepository<ClientUser, Long> {

    boolean existsByClientClientIdAndUserAccountUserId(Long clientId, Long userId);

    @EntityGraph(attributePaths = {"client", "userAccount"})
    List<ClientUser> findByClientClientIdOrderByCreatedAtAsc(Long clientId);

    @EntityGraph(attributePaths = {"client", "userAccount"})
    Optional<ClientUser> findByClientUserId(Long clientUserId);
}
