package org.syncspacers.syncspace.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.syncspacers.syncspace.model.Carpeta;
import org.syncspacers.syncspace.repository.CarpetaRepository;

@Service
public class CarpetaService {
    @Autowired
    CarpetaRepository carpetaRepository;

    /**
     * Guarda la carpeta en la base de datos
     *
     * @param archivo La carpeta a guardar
     */
    public void save(Carpeta carpeta) {
        carpetaRepository.saveAndFlush(carpeta);
    }

    /**
     * Devuelve la carpeta dado un identificador
     *
     * @param id El identificador de la carpeta
     * @return La carpeta asociada al identificador
     */
    public Optional<Carpeta> findById(String id) {
        try {
            return findById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<Carpeta> findById(Long id) {
        return carpetaRepository.findById(id);
    }
}
