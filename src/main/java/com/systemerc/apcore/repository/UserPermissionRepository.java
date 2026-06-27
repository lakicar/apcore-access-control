package com.systemerc.apcore.repository;

import com.systemerc.apcore.entity.UserPermission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    boolean existsByClientUserClientUserIdAndPermissionCodeAndAllowedTrue(Long clientUserId, String permissionCode);

    @EntityGraph(attributePaths = {"clientUser", "grantedByClientUser"})
    List<UserPermission> findByClientUserClientUserIdOrderByGrantedAtAsc(Long clientUserId);

    Optional<UserPermission> findByClientUserClientUserIdAndPermissionCode(Long clientUserId, String permissionCode);
}
