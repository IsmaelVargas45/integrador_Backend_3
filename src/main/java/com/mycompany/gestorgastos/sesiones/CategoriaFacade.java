/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorgastos.sesiones;

import com.mycompany.gestorgastos.Categoria;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
/**
 *
 * @author 54264
 */
@Stateless
public class CategoriaFacade extends AbstractFacade<Categoria> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaFacade() {
        super(Categoria.class);
    }
    

    public List<Categoria> findByUsuario(int idUsuario) {
        return em.createQuery(
                "SELECT c FROM Categoria c WHERE c.usuarioIdusuario.idusuario = :id ORDER BY c.tipo, c.nombre",
                Categoria.class)
                .setParameter("id", idUsuario)
                .getResultList();
    }

    public boolean tieneMovimientos(int idCategoria) {
        Long n = em.createQuery(
                "SELECT COUNT(m) FROM Movimiento m WHERE m.categoriaIdcategoria.idcategoria = :id",
                Long.class)
                .setParameter("id", idCategoria)
                .getSingleResult();
        return n > 0;
    }
}
