package org.syncspacers.syncspace.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.syncspacers.syncspace.model.Usuario;
import org.syncspacers.syncspace.repository.UsuarioRepository;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;

    /**
     * Clave: Token
     * Valor: email
     */
    private Map<String, String> userMap;

    public UsuarioService() {
        this.userMap = new HashMap<>();
    }

    /**
     * Devuelve una instancia de 'Usuario' para un determinado token
     *
     * @param token El token para el que se busca el usuario
     * @return La instancia de 'Usuario' envuelta en un 'Optional'
     */
    public Optional<Usuario> getByToken(String token) {
        String email = userMap.getOrDefault(token, "");
        return getByEmail(email);
    }

    /**
     * Devuelve una instancia de 'Usuario' para un determinado email
     *
     * @param email El email para el que se busca el usuario
     * @return La instancia de 'Usuario' envuelta en un 'Optional'
     */
    private Optional<Usuario> getByEmail(String email) {
        return usuarioRepository.findById(email);
    }

    /**
     * Guarda el usuario en la base de datos
     *
     * @param usuario El usuario a guardar
     */
    public void save(Usuario usuario) {
        usuarioRepository.saveAndFlush(usuario);
    }

    /**
     * Crea una sesión para un usuario
     *
     * @param email El email del usuario
     * @param password La contraseña del usuario
     * @return Un token si el email y la contraseña son correctos
     * @return Un string vacío si no existe el email o la contraseña es incorrecta
     */
    public String login(String email, String password) {
        Optional<Usuario> usuario = getByEmail(email);
        String token = Long.toString(new Random().nextLong());

        if (!usuario.isPresent()) {
            return "";
        }

        if (!usuario.get().getPassword().equals(password)) {
            return "";
        }

        userMap.put(token, usuario.get().getEmail());
        return token;
    }
}
