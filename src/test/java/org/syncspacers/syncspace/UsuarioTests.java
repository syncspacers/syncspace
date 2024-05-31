package org.syncspacers.syncspace;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.syncspacers.syncspace.model.Archivo;
import org.syncspacers.syncspace.model.Carpeta;
import org.syncspacers.syncspace.model.Usuario;

/*
 * Pruebas unitarias para usuario
 */
@SpringBootTest
class UsuarioTests {

	Usuario usuario1 = null;
    Usuario usuario2 = null;
    Archivo archivo1 = null;
    Carpeta carpeta1 = null;
    List<Archivo> listaArchivos1 = null;
    List<Carpeta> listaCarpetas1 = null;

	@BeforeEach
	void init() {
		usuario1 = new Usuario();
        usuario2 = new Usuario();
        archivo1 = new Archivo();
        carpeta1 = new Carpeta();
        listaArchivos1 = new LinkedList<>();
        listaCarpetas1 = new LinkedList<>();
	}

    @Test
    void testUsername() {
        String nombre = "usuario";
        usuario1.setUsername(nombre);
        assertTrue(nombre.equals(usuario1.getUsername()));
    }

    @Test
    void testPassword() {
        String passwd = "contraseña";
        usuario1.setPassword(passwd);
        assertTrue(passwd.equals(usuario1.getPassword()));
    }

    @Test
    void testEmail() {
        String email = "email@email.com";
        usuario1.setEmail(email);
        assertTrue(email.equals(usuario1.getEmail()));
    }

    @Test
    void testEquals() {
        usuario1.setEmail("email@email.com");
        usuario2.setEmail("email@email.com");

        assertTrue(usuario1.equals(usuario2));
    }

    @Test
    void testArchivos() {
        assertThrows(RuntimeException.class, () -> usuario1.setArchivos(null));

        assertNotNull(usuario1.getArchivos());
        usuario1.getArchivos().add(archivo1);
        assertTrue(usuario1.getArchivos().size() == 1);
        usuario1.setArchivos(listaArchivos1);
        assertTrue(usuario1.getArchivos().size() == 0);
    }

    @Test
    void testCarpetas() {
        assertThrows(RuntimeException.class, () -> usuario1.setCarpetas(null));
        
        assertNotNull(usuario1.getCarpetas());
        usuario1.getCarpetas().add(carpeta1);
        assertTrue(usuario1.getCarpetas().size() == 1);
        usuario1.setCarpetas(listaCarpetas1);
        assertTrue(usuario1.getCarpetas().size() == 0);
    }
}
