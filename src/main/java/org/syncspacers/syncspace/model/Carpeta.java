package org.syncspacers.syncspace.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Carpeta {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Usuario usuario;

    @OneToMany (mappedBy = "carpeta", cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<Archivo> archivos;

    @OneToMany (mappedBy = "carpetaPadre", cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private List<Carpeta> carpetas;

    @ManyToOne
    private Carpeta carpetaPadre;

    private boolean enPapelera;

    private Date fechaDeSubida;

    private String nombre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Archivo> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Archivo> archivos) {
        this.archivos = archivos;
    }

    public boolean isEnPapelera() {
        return enPapelera;
    }

    public void setEnPapelera(boolean enPapelera) {
        this.enPapelera = enPapelera;
    }

    public Date getFechaDeSubida() {
        return fechaDeSubida;
    }

    public void setFechaDeSubida(Date fechaDeSubida) {
        this.fechaDeSubida = fechaDeSubida;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Carpeta> getCarpetas() {
        return carpetas;
    }

    public void setCarpetas(List<Carpeta> carpetas) {
        this.carpetas = carpetas;
    }

    public Carpeta getCarpetaPadre() {
        return carpetaPadre;
    }

    public void setCarpetaPadre(Carpeta carpetaPadre) {
        this.carpetaPadre = carpetaPadre;
    }
}
