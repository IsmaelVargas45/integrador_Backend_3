/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorgastos;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author 54264
 */
@Entity
@Table(name = "categoria")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Categoria.findAll", query = "SELECT c FROM Categoria c"),
    @NamedQuery(name = "Categoria.findByIdcategoria", query = "SELECT c FROM Categoria c WHERE c.idcategoria = :idcategoria"),
    @NamedQuery(name = "Categoria.findByNombre", query = "SELECT c FROM Categoria c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Categoria.findByTipo", query = "SELECT c FROM Categoria c WHERE c.tipo = :tipo")})
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idcategoria")
    private Integer idcategoria;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 7)
    @Column(name = "tipo")
    private String tipo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoriaIdcategoria")
    private Collection<Movimiento> movimientoCollection;
    @JoinColumn(name = "usuario_idusuario", referencedColumnName = "idusuario")
    @ManyToOne(optional = false)
    private Usuario usuarioIdusuario;

    public Categoria() {
    }

    public Categoria(Integer idcategoria) {
        this.idcategoria = idcategoria;
    }

    public Categoria(Integer idcategoria, String nombre, String tipo) {
        this.idcategoria = idcategoria;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public Integer getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(Integer idcategoria) {
        this.idcategoria = idcategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    public Collection<Movimiento> getMovimientoCollection() {
        return movimientoCollection;
    }

    public void setMovimientoCollection(Collection<Movimiento> movimientoCollection) {
        this.movimientoCollection = movimientoCollection;
    }

    public Usuario getUsuarioIdusuario() {
        return usuarioIdusuario;
    }

    public void setUsuarioIdusuario(Usuario usuarioIdusuario) {
        this.usuarioIdusuario = usuarioIdusuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcategoria != null ? idcategoria.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Categoria)) {
            return false;
        }
        Categoria other = (Categoria) object;
        if ((this.idcategoria == null && other.idcategoria != null) || (this.idcategoria != null && !this.idcategoria.equals(other.idcategoria))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.gestorgastos.Categoria[ idcategoria=" + idcategoria + " ]";
    }
    
}
