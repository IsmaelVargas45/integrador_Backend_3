/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorgastos.sesiones;

import com.mycompany.gestorgastos.Movimiento;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import jakarta.persistence.TypedQuery;
/**
 *
 * @author 54264
 */
@Stateless
public class MovimientoFacade extends AbstractFacade<Movimiento> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MovimientoFacade() {
        super(Movimiento.class);
    }
 
    public List<Object[]> totalPorCategoria(int idUsuario, String tipo, Date desde, Date hasta) {
        return em.createQuery(
                "SELECT c.nombre, SUM(m.monto) FROM Movimiento m JOIN m.categoriaIdcategoria c "
                + "WHERE m.usuarioIdusuario.idusuario = :id AND c.tipo = :tipo "
                + "AND m.fecha BETWEEN :desde AND :hasta "
                + "GROUP BY c.idcategoria, c.nombre ORDER BY SUM(m.monto) DESC", Object[].class)
                .setParameter("id", idUsuario)
                .setParameter("tipo", tipo)
                .setParameter("desde", desde, TemporalType.DATE)
                .setParameter("hasta", hasta, TemporalType.DATE)
                .getResultList();
    }

    public BigDecimal total(int idUsuario, String tipo, Date desde, Date hasta) {
        BigDecimal r = em.createQuery(
                "SELECT SUM(m.monto) FROM Movimiento m "
                + "WHERE m.usuarioIdusuario.idusuario = :id AND m.categoriaIdcategoria.tipo = :tipo "
                + "AND m.fecha BETWEEN :desde AND :hasta", BigDecimal.class)
                .setParameter("id", idUsuario)
                .setParameter("tipo", tipo)
                .setParameter("desde", desde, TemporalType.DATE)
                .setParameter("hasta", hasta, TemporalType.DATE)
                .getSingleResult();
        return r == null ? BigDecimal.ZERO : r;
    }
        public List<Movimiento> filtrar(int idUsuario, Date desde, Date hasta, Integer idCategoria) {
        StringBuilder jpql = new StringBuilder(
                "SELECT m FROM Movimiento m WHERE m.usuarioIdusuario.idusuario = :id");
        if (desde != null) {
            jpql.append(" AND m.fecha >= :desde");
        }
        if (hasta != null) {
            jpql.append(" AND m.fecha <= :hasta");
        }
        if (idCategoria != null) {
            jpql.append(" AND m.categoriaIdcategoria.idcategoria = :cat");
        }
        jpql.append(" ORDER BY m.fecha DESC, m.idmovimiento DESC");

        TypedQuery<Movimiento> q = em.createQuery(jpql.toString(), Movimiento.class);
        q.setParameter("id", idUsuario);
        if (desde != null) {
            q.setParameter("desde", desde, TemporalType.DATE);
        }
        if (hasta != null) {
            q.setParameter("hasta", hasta, TemporalType.DATE);
        }
        if (idCategoria != null) {
            q.setParameter("cat", idCategoria);
        }
        return q.getResultList();
    }
}
