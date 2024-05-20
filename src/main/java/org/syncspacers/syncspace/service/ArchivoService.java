package org.syncspacers.syncspace.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.syncspacers.syncspace.model.Archivo;
import org.syncspacers.syncspace.repository.ArchivoRepository;

@Service
public class ArchivoService {
    @Autowired
    ArchivoRepository archivoRepository;

    /**
     * Guarda el archivo en la base de datos
     *
     * @param archivo El archivo a guardar
     */
    public void save(Archivo archivo) {
        archivoRepository.saveAndFlush(archivo);
    }

    public void delete(Archivo archivo) {
        archivoRepository.delete(archivo);
        archivoRepository.flush();
    }

    /**
     * Devuelve el archivo dado un identificador
     *
     * @param id El identificador del archivo
     * @return El archivo asociado al identificador
     */
    public Optional<Archivo> findById(Long id) {
        if (id == null) return Optional.empty();
        return archivoRepository.findById(id);
    }
}
