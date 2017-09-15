package de.fraunhofer.abm.collection.dao.jpa;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.RepositoryDao;
import de.fraunhofer.abm.collection.dao.RepositoryPropertyDao;
import de.fraunhofer.abm.domain.RepositoryDTO;

@Component
public class JpaRepositoryDao implements RepositoryDao {

    @Reference
    TransactionControl transactionControl;

    @Reference(name="provider")
    JPAEntityManagerProvider jpaEntityManagerProvider;

    EntityManager em;

    @Reference
    RepositoryPropertyDao propDao;

    @Activate
    void start(Map<String, Object> props) {
        try {
            em = jpaEntityManagerProvider.getResource(transactionControl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<RepositoryDTO> select() {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaRepository> query = em.createQuery("SELECT r FROM repository r", JpaRepository.class);
            query.setMaxResults(100);
            List<JpaRepository> jpaList = query.getResultList();
            return jpaList.stream().map(JpaRepository::toDTO).collect(Collectors.toList());
        });
    }

    @Override
    public List<RepositoryDTO> findByCollection(String collection) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaRepository> query = em.createQuery("SELECT r FROM repository r WHERE r.collection_id = :collectionId ORDER BY r.name", JpaRepository.class);
            query.setParameter("collectionId", collection);
            List<JpaRepository> jpaList = query.getResultList();
            return jpaList.stream().map(JpaRepository::toDTO).collect(Collectors.toList());
        });
    }

    @Override
    public RepositoryDTO findById(String id) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaRepository> query = em.createQuery("SELECT r FROM repository r WHERE r.id = :id ORDER BY r.name", JpaRepository.class);
            query.setParameter("id", id);
            JpaRepository result = query.getSingleResult();
            return result.toDTO();
        });
    }
    
    @Override
    public RepositoryDTO findByVersion(String versionId)
    {
    	return transactionControl.notSupported(() -> {
        TypedQuery<JpaRepository> query = em.createQuery("select c from repository c join c.commits as cmt join cmt.version as v where v.id = :versionId", JpaRepository.class);
        query.setParameter("versionId", versionId);
        JpaRepository result = query.getSingleResult();
        return result.toDTO();
    });
		
    	
    	
    }

    @Override
    public void save(RepositoryDTO repository) {
        transactionControl.required(() -> {
            em.persist(JpaRepository.fromDTO(repository));
            return 0;
        });
    }

    @Override
    public void update(RepositoryDTO repository) {
        transactionControl.required(() -> {
            em.merge(JpaRepository.fromDTO(repository));
            return 0;
        });
    }

    @Override
    public void delete(String id) {
        transactionControl.required(() -> {
            // delete the repo
            JpaRepository repository = em.find(JpaRepository.class, id);
            em.remove(repository);
            return 0;
        });
    }
}
