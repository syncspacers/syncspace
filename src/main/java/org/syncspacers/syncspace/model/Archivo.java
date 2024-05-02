package org.syncspacers.syncspace.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Archivo {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Usuario usuario;

    private boolean enPapelera;

    private Date fechaDeSubida;

    private String nombre;
    private String comentario;

    /**
     * @return True si el archivo está en la papelera
     */
    public boolean isEnPapelera() {
        return enPapelera;
    }

    /**
     * Establece si el archivo está en la papelera
     * @param enPapelera True si el archivo está en la papelera
     */
    public void setEnPapelera(boolean enPapelera) {
        this.enPapelera = enPapelera;
    }

    /**
     * @return Devuelve el identificador del archivo
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del archivo
     * @param id El identificador del archivo
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return La fecha de subida del archivo
     */
    public Date getFechaDeSubida() {
        return fechaDeSubida;
    }

    /**
     * Establece la fecha de subida del archivo
     * @param fechaDeSubida La fecha de subida del archivo
     */
    public void setFechaDeSubida(Date fechaDeSubida) {
        this.fechaDeSubida = fechaDeSubida;
    }

    /**
     * @return El nombre del archivo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del archivo
     * @param nombre El nombre del archivo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return El comentario del archivo
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * Establece el comentario del archivo
     * @param comentario El comentario del archivo
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    /**
     * @return El usuario que posee el archivo
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Establece el usuario que posee el archivo
     * @param usuario El usuario que posee el archivo
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
