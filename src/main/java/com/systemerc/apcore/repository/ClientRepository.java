package com.systemerc.apcore.repository;

import com.systemerc.apcore.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByTaxId(String taxId);

    Optional<Client> findByTaxId(String taxId);
}
