package co.edu.unab.misiontic.pruebac3.jpacontroller;

import co.edu.unab.misiontic.pruebac3.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.edu.unab.misiontic.pruebac3.model.Equipo;
import co.edu.unab.misiontic.pruebac3.model.Partido;
import co.edu.unab.misiontic.pruebac3.model.Usuario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Laura Pulido
 */
public class PartidosJpaController implements Serializable {

    public PartidosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Partido partidos) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Equipo local = partidos.getLocal();
            if (local != null) {
                local = em.getReference(local.getClass(), local.getId());
                partidos.setLocal(local);
            }
            Equipo visitante = partidos.getVisitante();
            if (visitante != null) {
                visitante = em.getReference(visitante.getClass(), visitante.getId());
                partidos.setVisitante(visitante);
            }
            Usuario usuario = partidos.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getId());
                partidos.setUsuario(usuario);
            }
            em.persist(partidos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Partido partidos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Partido persistentPartido = em.find(Partido.class, partidos.getId());
            Equipo localOld = persistentPartido.getLocal();
            Equipo localNew = partidos.getLocal();
            Equipo visitanteOld = persistentPartido.getVisitante();
            Equipo visitanteNew = partidos.getVisitante();
            Usuario usuarioOld = persistentPartido.getUsuario();
            Usuario usuarioNew = partidos.getUsuario();
            if (localNew != null) {
                localNew = em.getReference(localNew.getClass(), localNew.getId());
                partidos.setLocal(localNew);
            }
            if (visitanteNew != null) {
                visitanteNew = em.getReference(visitanteNew.getClass(), visitanteNew.getId());
                partidos.setVisitante(visitanteNew);
            }
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getId());
                partidos.setUsuario(usuarioNew);
            }
            partidos = em.merge(partidos);
            if (localOld != null && !localOld.equals(localNew)) {
                localOld.getPartidoList().remove(partidos);
                localOld = em.merge(localOld);
            }
            if (localNew != null && !localNew.equals(localOld)) {
                localNew.getPartidoList().add(partidos);
                localNew = em.merge(localNew);
            }
            if (visitanteOld != null && !visitanteOld.equals(visitanteNew)) {
                visitanteOld.getPartidoList().remove(partidos);
                visitanteOld = em.merge(visitanteOld);
            }
            if (visitanteNew != null && !visitanteNew.equals(visitanteOld)) {
                visitanteNew.getPartidoList().add(partidos);
                visitanteNew = em.merge(visitanteNew);
            }
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.getPartidoList().remove(partidos);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.getPartidoList().add(partidos);
                usuarioNew = em.merge(usuarioNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = partidos.getId();
                if (findPartido(id) == null) {
                    throw new NonexistentEntityException("The partidos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Partido partidos;
            try {
                partidos = em.getReference(Partido.class, id);
                partidos.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The partidos with id " + id + " no longer exists.", enfe);
            }
            Equipo local = partidos.getLocal();
            if (local != null) {
                local.getPartidoList().remove(partidos);
                local = em.merge(local);
            }
            Equipo visitante = partidos.getVisitante();
            if (visitante != null) {
                visitante.getPartidoList().remove(partidos);
                visitante = em.merge(visitante);
            }
            Usuario usuario = partidos.getUsuario();
            if (usuario != null) {
                usuario.getPartidoList().remove(partidos);
                usuario = em.merge(usuario);
            }
            em.remove(partidos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Partido> findPartidoEntities() {
        return findPartidoEntities(true, -1, -1);
    }

    public List<Partido> findPartidoEntities(int maxResults, int firstResult) {
        return findPartidoEntities(false, maxResults, firstResult);
    }

    private List<Partido> findPartidoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Partido.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Partido findPartido(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Partido.class, id);
        } finally {
            em.close();
        }
    }

    public int getPartidoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Partido> rt = cq.from(Partido.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
