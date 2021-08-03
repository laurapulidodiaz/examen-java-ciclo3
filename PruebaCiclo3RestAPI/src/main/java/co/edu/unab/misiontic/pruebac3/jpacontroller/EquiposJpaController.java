package co.edu.unab.misiontic.pruebac3.jpacontroller;

import co.edu.unab.misiontic.pruebac3.exceptions.IllegalOrphanException;
import co.edu.unab.misiontic.pruebac3.exceptions.NonexistentEntityException;
import co.edu.unab.misiontic.pruebac3.model.Equipo;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.edu.unab.misiontic.pruebac3.model.Partido;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Laura Pulido
 */
public class EquiposJpaController implements Serializable {

    public EquiposJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public void create(Equipo equipos) {
        if (equipos.getPartidoList()== null) {
            equipos.setPartidoList(new ArrayList<Partido>());
        }
        if (equipos.getPartidoList1() == null) {
            equipos.setPartidoList1(new ArrayList<Partido>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Partido> attachedPartidoList = new ArrayList<Partido>();
            for (Partido partidosListPartidoToAttach : equipos.getPartidoList()) {
                partidosListPartidoToAttach = em.getReference(partidosListPartidoToAttach.getClass(), partidosListPartidoToAttach.getId());
                attachedPartidoList.add(partidosListPartidoToAttach);
            }
            equipos.setPartidoList(attachedPartidoList);
            List<Partido> attachedPartidoList1 = new ArrayList<Partido>();
            for (Partido partidosList1PartidoToAttach : equipos.getPartidoList1()) {
                partidosList1PartidoToAttach = em.getReference(partidosList1PartidoToAttach.getClass(), partidosList1PartidoToAttach.getId());
                attachedPartidoList1.add(partidosList1PartidoToAttach);
            }
            equipos.setPartidoList1(attachedPartidoList1);
            em.persist(equipos);
            for (Partido partidosListPartido : equipos.getPartidoList()) {
                Equipo oldLocalOfPartidoListPartido = partidosListPartido.getLocal();
                partidosListPartido.setLocal(equipos);
                partidosListPartido = em.merge(partidosListPartido);
                if (oldLocalOfPartidoListPartido != null) {
                    oldLocalOfPartidoListPartido.getPartidoList().remove(partidosListPartido);
                    oldLocalOfPartidoListPartido = em.merge(oldLocalOfPartidoListPartido);
                }
            }
            for (Partido partidosList1Partido : equipos.getPartidoList1()) {
                Equipo oldVisitanteOfPartidoList1Partido = partidosList1Partido.getVisitante();
                partidosList1Partido.setVisitante(equipos);
                partidosList1Partido = em.merge(partidosList1Partido);
                if (oldVisitanteOfPartidoList1Partido != null) {
                    oldVisitanteOfPartidoList1Partido.getPartidoList1().remove(partidosList1Partido);
                    oldVisitanteOfPartidoList1Partido = em.merge(oldVisitanteOfPartidoList1Partido);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Equipo equipos) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Equipo persistentEquipos = em.find(Equipo.class, equipos.getId());
            List<Partido> partidosListOld = persistentEquipos.getPartidoList();
            List<Partido> partidosListNew = equipos.getPartidoList();
            List<Partido> partidosList1Old = persistentEquipos.getPartidoList1();
            List<Partido> partidosList1New = equipos.getPartidoList1();
            List<String> illegalOrphanMessages = null;
            for (Partido partidosListOldPartido : partidosListOld) {
                if (!partidosListNew.contains(partidosListOldPartido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Partido " + partidosListOldPartido + " since its local field is not nullable.");
                }
            }
            for (Partido partidosList1OldPartido : partidosList1Old) {
                if (!partidosList1New.contains(partidosList1OldPartido)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Partido " + partidosList1OldPartido + " since its visitante field is not nullable.");
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
            equipos.setPartidoList(partidosListNew);
            List<Partido> attachedPartidoList1New = new ArrayList<Partido>();
            for (Partido partidosList1NewPartidoToAttach : partidosList1New) {
                partidosList1NewPartidoToAttach = em.getReference(partidosList1NewPartidoToAttach.getClass(), partidosList1NewPartidoToAttach.getId());
                attachedPartidoList1New.add(partidosList1NewPartidoToAttach);
            }
            partidosList1New = attachedPartidoList1New;
            equipos.setPartidoList1(partidosList1New);
            equipos = em.merge(equipos);
            for (Partido partidosListNewPartido : partidosListNew) {
                if (!partidosListOld.contains(partidosListNewPartido)) {
                    Equipo oldLocalOfPartidoListNewPartido = partidosListNewPartido.getLocal();
                    partidosListNewPartido.setLocal(equipos);
                    partidosListNewPartido = em.merge(partidosListNewPartido);
                    if (oldLocalOfPartidoListNewPartido != null && !oldLocalOfPartidoListNewPartido.equals(equipos)) {
                        oldLocalOfPartidoListNewPartido.getPartidoList().remove(partidosListNewPartido);
                        oldLocalOfPartidoListNewPartido = em.merge(oldLocalOfPartidoListNewPartido);
                    }
                }
            }
            for (Partido partidosList1NewPartido : partidosList1New) {
                if (!partidosList1Old.contains(partidosList1NewPartido)) {
                    Equipo oldVisitanteOfPartidoList1NewPartido = partidosList1NewPartido.getVisitante();
                    partidosList1NewPartido.setVisitante(equipos);
                    partidosList1NewPartido = em.merge(partidosList1NewPartido);
                    if (oldVisitanteOfPartidoList1NewPartido != null && !oldVisitanteOfPartidoList1NewPartido.equals(equipos)) {
                        oldVisitanteOfPartidoList1NewPartido.getPartidoList1().remove(partidosList1NewPartido);
                        oldVisitanteOfPartidoList1NewPartido = em.merge(oldVisitanteOfPartidoList1NewPartido);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = equipos.getId();
                if (findEquipos(id) == null) {
                    throw new NonexistentEntityException("The equipos with id " + id + " no longer exists.");
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
            Equipo equipos;
            try {
                equipos = em.getReference(Equipo.class, id);
                equipos.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The equipos with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Partido> partidosListOrphanCheck = equipos.getPartidoList();
            for (Partido partidosListOrphanCheckPartido : partidosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Equipos (" + equipos + ") cannot be destroyed since the Partido " + partidosListOrphanCheckPartido + " in its partidosList field has a non-nullable local field.");
            }
            List<Partido> partidosList1OrphanCheck = equipos.getPartidoList1();
            for (Partido partidosList1OrphanCheckPartido : partidosList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Equipos (" + equipos + ") cannot be destroyed since the Partido " + partidosList1OrphanCheckPartido + " in its partidosList1 field has a non-nullable visitante field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(equipos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Equipo> findEquiposEntities() {
        return findEquiposEntities(true, -1, -1);
    }

    public List<Equipo> findEquiposEntities(int maxResults, int firstResult) {
        return findEquiposEntities(false, maxResults, firstResult);
    }

    private List<Equipo> findEquiposEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Equipo.class));
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

    public Equipo findEquipos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Equipo.class, id);
        } finally {
            em.close();
        }
    }

    public int getEquiposCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Equipo> rt = cq.from(Equipo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
