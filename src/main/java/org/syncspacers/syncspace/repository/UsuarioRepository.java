package org.syncspacers.syncspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.syncspacers.syncspace.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}
