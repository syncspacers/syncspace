package org.syncspacers.syncspace;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.syncspacers.syncspace.model.Usuario;
import org.syncspacers.syncspace.repository.UsuarioRepository;
import org.syncspacers.syncspace.service.UsuarioService;


/*
 * Pruebas de integración para vaios casos de uso
 */
@SpringBootTest
class IntegrationTests {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    UsuarioRepository usuarioRepository;

    String nombre = "usuario";
    String email = "email@email.com";
    String password = "password";

    /*
     * Esto incluye trés casos de uso: login, register y logout
     */
    @Test
    void testSession() {
        Usuario usuario = new Usuario();

        usuario.setUsername(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);

        // Registramos el usuario
        usuarioService.save(usuario);

        // Iniciamos sesión
        String token = usuarioService.login(email, password);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Cerramos la sesión
        usuarioService.logout(token);
        Optional<Usuario> usuarioData = usuarioService.getByToken(token);
        assertFalse(usuarioData.isPresent());
    }
}
