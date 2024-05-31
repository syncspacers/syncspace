package org.syncspacers.syncspace;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.syncspacers.syncspace.model.Archivo;
import org.syncspacers.syncspace.model.Carpeta;
import org.syncspacers.syncspace.model.Usuario;

/*
 * Pruebas unitarias para archivo
 */
@SpringBootTest
class ArchivoTests {

	Usuario usuario1 = null;
    Archivo archivo1 = null;
    Archivo archivo2 = null;
    Carpeta carpeta1 = null;

	@BeforeEach
	void init() {
		usuario1 = new Usuario();
        archivo1 = new Archivo();
        archivo2 = new Archivo();
        carpeta1 = new Carpeta();
	}

    @Test
    void testId() {
        Long id = 9999L;
        archivo1.setId(id);
        assertTrue(archivo1.getId() == id);
    }

    @Test
    void testUsuario() {
        assertNull(archivo1.getUsuario());
        archivo1.setUsuario(usuario1);
        assertNotNull(archivo1.getUsuario());
    }

    @Test
    void testCarpeta() {
        assertNull(archivo1.getCarpeta());
        archivo1.setCarpeta(carpeta1);
        assertNotNull(archivo1.getCarpeta());
    }

    @Test
    void testNombre() {
        String nombre = "archivo1";
        archivo1.setNombre(nombre);
        assertTrue(nombre.equals(archivo1.getNombre()));
    }

    @Test
    void testContenido() {
        byte[] contenido = new byte[10];

        assertNull(archivo1.getContenido());
        archivo1.setContenido(contenido);
        assertNotNull(archivo1.getContenido());
        assertTrue(archivo1.getContenido().length == contenido.length);
    }

    @Test
    void testEnPapelera() {
        assertFalse(archivo1.isEnPapelera());
        archivo1.setEnPapelera(true);
        assertTrue(archivo1.isEnPapelera());
    }
}
