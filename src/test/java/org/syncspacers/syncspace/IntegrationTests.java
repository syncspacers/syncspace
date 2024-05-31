package org.syncspacers.syncspace;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.syncspacers.syncspace.controller.UsuarioController;
import org.syncspacers.syncspace.model.Carpeta;
import org.syncspacers.syncspace.model.Usuario;
import org.syncspacers.syncspace.repository.CarpetaRepository;
import org.syncspacers.syncspace.service.CarpetaService;
import org.syncspacers.syncspace.service.UsuarioService;

/*
 * Pruebas de integración para vaios casos de uso
 */
@SpringBootTest
class IntegrationTests {
    @Autowired
    UsuarioController controller;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    CarpetaService carpetaService;

    @Autowired
    CarpetaRepository carpetaRepository;

    String nombre = "usuario";
    String email = "email@email.com";
    String password = "password";
    String newPassword = "newPassword";

    /*
     * Esto incluye los siguientes casos de uso: login, register, logout, cambiar contraseña
     */
    @Test
    void testIntegration() {
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

        // Cambiar contraseña
        assertTrue(password.equals(usuario.getPassword()));
        controller.changePassword(token, password, newPassword, newPassword);
        usuario = usuarioService.getByToken(token).get();
        assertTrue(newPassword.equals(usuario.getPassword()));

        // Crear carpeta
        int numCarpetasIni = carpetaRepository.findAll().size();
        controller.createFolder("carpeta", token, -1L);
        int numCarpetas = carpetaRepository.findAll().size();
        assertTrue(numCarpetas > numCarpetasIni);

        // Borrar carpeta
        Carpeta carpeta = carpetaRepository.findAll().get(0);
        carpetaService.delete(carpeta);
        numCarpetas = carpetaRepository.findAll().size();
        assertTrue(numCarpetas == numCarpetasIni);

        // Cerramos la sesión
        usuarioService.logout(token);
        Optional<Usuario> usuarioData = usuarioService.getByToken(token);
        assertFalse(usuarioData.isPresent());
    }
}
