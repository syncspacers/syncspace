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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Pruebas unitarias para archivo
 */
@SpringBootTest
class FolderTests {

    Usuario usuario1 = null;
    Archivo archivo1 = null;
    Archivo archivo2 = null;
    Carpeta carpeta1 = null;
    Carpeta carpeta2 = null;

    @BeforeEach
    void init() {
        usuario1 = new Usuario();
        usuario1.setEmail("usuario@gmail.com");
        archivo1 = new Archivo();
        archivo2 = new Archivo();
        carpeta1 = new Carpeta();
        carpeta2 = new Carpeta();
    }

    @Test
    void testId() {
        Long id = 9999L;
        carpeta1.setId(id);
        assertTrue(carpeta1.getId() == id);
    }

    @Test
    void testArchivos() {
        List<Archivo> archivos = new ArrayList<Archivo>();
        archivos.add(archivo1);
        archivos.add(archivo2);
        carpeta1.setArchivos(archivos);
        assertTrue(carpeta1.getArchivos().size()==2);
    }

    @Test
    void renameCarpeta() {
        carpeta1.setNombre("prueba");
        assertTrue(carpeta1.getNombre().equals("prueba"));
    }

    @Test
    void PapeleraCarpeta() {
        carpeta1.setEnPapelera(true);
        assertTrue(carpeta1.isEnPapelera());
        carpeta1.setEnPapelera(false);
        assertFalse(carpeta1.isEnPapelera());
    }

    @Test
    void CarpetaPadre() {
        carpeta1.setCarpetaPadre(carpeta2);
        assertTrue(carpeta1.getCarpetaPadre().equals(carpeta2));
    }

    @Test
    void UsuarioCarpeta() {
        carpeta1.setUsuario(usuario1);
        assertTrue(carpeta1.getUsuario().equals(usuario1));
    }
}
