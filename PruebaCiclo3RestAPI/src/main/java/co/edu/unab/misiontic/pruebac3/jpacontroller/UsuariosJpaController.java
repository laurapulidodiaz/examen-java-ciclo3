package co.edu.unab.misiontic.pruebac3.jpacontroller;

import co.edu.unab.misiontic.pruebac3.exceptions.IllegalOrphanException;
import co.edu.unab.misiontic.pruebac3.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.edu.unab.misiontic.pruebac3.model.Partido;
import co.edu.unab.misiontic.pruebac3.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Laura Pulido
 */
public class UsuariosJpaController implements Serializable {

    public UsuariosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuarios) {
        if (usuarios.getPartidoList() == null) {
            usuarios.setPartidoList(new ArrayList<Partido>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Partido> attachedPartidoList = new ArrayList<Partido>();
            for (Partido partidosListPartidoToAttach : usuarios.getPartidoList()) {
                partidosListPartidoToAttach = em.getReference(partidosListPartidoToAttach.getClass(), partidosListPartidoToAttach.getId());
                attachedPartidoList.add(partidosListPartidoToAttach);
            }
            usuarios.setPartidoList(attachedPartidoList);
            em.persist(usuarios);
            for (Partido partidosListPartido : usuarios.getPartidoList()) {
                Usuario oldUsuarioOfPartidoListPartido = partidosListPartido.getUsuario();
                partidosListPartido.setUsuario(usuarios);
                partidosListPartido = em.merge(partidosListPartido);
                if (oldUsuarioOfPartidoListPartido != null) {
                    oldUsuarioOfPartidoListPartido.getPartidoList().remove(partidosListPartido);
                    oldUsuarioOfPartidoListPartido = em.merge(oldUsuarioOfPartidoListPartido);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuarios) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuarios.getId());
            List<Partido> partidosListOld = persistentUsuario.getPartidoList();
            List<Partido> partidosListNew = usuarios.getPartidoList();
            List<String> illegalOrphanMessages = null;
            for (Partido partidosListOldPartido : partidosListOld) {
                if (!partidosListNew.contains(partidosListOldPartido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Partido " + partidosListOldPartido + " since its usuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Partido> attachedPartidoListNew = new ArrayList<Partido>();
            for (Partido partidosListNewPartidoToAttach : partidosListNew) {
                partidosListNewPartidoToAttach = em.getReference(partidosListNewPartidoToAttach.getClass(), partidosListNewPartidoToAttach.getId());
                attachedPartidoListNew.add(partidosListNewPartidoToAttach);
            }
            partidosListNew = attachedPartidoListNew;
            usuarios.setPartidoList(partidosListNew);
            usuarios = em.merge(usuarios);
            for (Partido partidosListNewPartido : partidosListNew) {
                if (!partidosListOld.contains(partidosListNewPartido)) {
                    Usuario oldUsuarioOfPartidoListNewPartido = partidosListNewPartido.getUsuario();
                    partidosListNewPartido.setUsuario(usuarios);
                    partidosListNewPartido = em.merge(partidosListNewPartido);
                    if (oldUsuarioOfPartidoListNewPartido != null && !oldUsuarioOfPartidoListNewPartido.equals(usuarios)) {
                        oldUsuarioOfPartidoListNewPartido.getPartidoList().remove(partidosListNewPartido);
                        oldUsuarioOfPartidoListNewPartido = em.merge(oldUsuarioOfPartidoListNewPartido);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuarios.getId();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuarios;
            try {
                usuarios = em.getReference(Usuario.class, id);
                usuarios.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Partido> partidosListOrphanCheck = usuarios.getPartidoList();
            for (Partido partidosListOrphanCheckPartido : partidosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuarios + ") cannot be destroyed since the Partido " + partidosListOrphanCheckPartido + " in its partidosList field has a non-nullable usuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuarios);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
