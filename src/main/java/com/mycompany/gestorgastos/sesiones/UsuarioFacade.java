/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorgastos.sesiones;

import com.mycompany.gestorgastos.Usuario;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
/**
 *
 * @author 54264
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }
       public Usuario findByEmail(String email) {
        List<Usuario> lista = em.createNamedQuery("Usuario.findByEmail", Usuario.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }
    
}
