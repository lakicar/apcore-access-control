package com.systemerc.apcore.repository;

import com.systemerc.apcore.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByTaxId(String taxId);

    default boolean existsByTaxIdOrRegistrationNumber(String taxId, String registrationNumber) {
        if (registrationNumber == null || registrationNumber.isBlank()) {
            return existsByTaxId(taxId);
        }
        return existsByTaxIdOrRegistrationNumberValue(taxId, registrationNumber);
    }

    @Query("""
            select case when count(c) > 0 then true else false end
            from Client c
            where c.taxId = :taxId
               or c.registrationNumber = :registrationNumber
            """)
    boolean existsByTaxIdOrRegistrationNumberValue(
            @Param("taxId") String taxId,
            @Param("registrationNumber") String registrationNumber
    );

    Optional<Client> findByTaxId(String taxId);
}
