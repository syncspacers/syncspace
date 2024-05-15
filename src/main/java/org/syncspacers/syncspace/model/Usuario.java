package org.syncspacers.syncspace.model;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Usuario {
    @Id
    private String email;

    @OneToMany (mappedBy = "usuario", cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<Archivo> archivos;

    @OneToMany (mappedBy = "usuario", cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<Carpeta> carpetas;

    private String username;
    private String password;

    public Usuario() {
        this.archivos = new LinkedList<>();
    }

    /**
     * @return El nombre de usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario
     * @param username El nombre de usuario
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return El email del usuario
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email del usuario
     * @param email El email del usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return La contraseña del usuario
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario
     * @param password La contraseña del usuario
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return La lista de archivos del usuario
     */
    public List<Archivo> getArchivos() {
        return archivos;
    }

    /**
     * Establece la lista de archivos del usuario
     * @param archivos La lista de archivos del usuario
     */
    public void setArchivos(List<Archivo> archivos) {
        this.archivos = archivos;
    }

    public List<Carpeta> getCarpetas() {
        return carpetas;
    }

    public void setCarpetas(List<Carpeta> carpetas) {
        this.carpetas = carpetas;
    }
}
