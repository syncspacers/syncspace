package org.syncspacers.syncspace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.syncspacers.syncspace.model.Archivo;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
    @Query("SELECT a FROM Archivo a WHERE a.publicID = :publicID")
    Optional<Archivo> findByPublicId(@Param("publicID") Long publicID);

    @Query("SELECT a FROM Archivo a WHERE a.passwordID = :passwordID")
    Optional<Archivo> findByPasswordId(@Param("passwordID") Long passwordID);
}
