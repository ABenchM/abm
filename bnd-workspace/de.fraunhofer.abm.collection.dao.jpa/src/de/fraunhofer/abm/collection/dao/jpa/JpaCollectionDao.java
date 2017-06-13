package de.fraunhofer.abm.collection.dao.jpa;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.CollectionDao;
import de.fraunhofer.abm.domain.CollectionDTO;

@Component
public class JpaCollectionDao extends AbstractJpaDao implements CollectionDao {

    @Reference
    TransactionControl transactionControl;

    @Reference(name = "provider")
    JPAEntityManagerProvider jpaEntityManagerProvider;

    EntityManager em;

    @Activate
    void start(Map<String, Object> props) {
        try {
            em = jpaEntityManagerProvider.getResource(transactionControl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CollectionDTO> select() {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaCollection> query = em.createQuery("SELECT c FROM collection c", JpaCollection.class);
            query.setMaxResults(100);
            List<JpaCollection> jpaList = query.getResultList();
            return jpaList.stream().map(JpaCollection::toDTO).collect(Collectors.toList());
        });
    }

    @Override
    public List<CollectionDTO> findByUser(String user) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaCollection> query = em.createQuery("SELECT c FROM collection c WHERE c.user = :user ORDER BY c.name", JpaCollection.class);
            query.setParameter("user", user);
            List<JpaCollection> jpaList = query.getResultList();
            return jpaList.stream().map(JpaCollection::toDTO).collect(Collectors.toList());
        });
    }

    @Override
    public CollectionDTO findById(String id) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaCollection> query = em.createQuery("SELECT c FROM collection c WHERE c.id = :id ORDER BY c.name", JpaCollection.class);
            query.setParameter("id", id);
            JpaCollection result = query.getSingleResult();
            return result.toDTO();
        });
    }
    
    @Override
    public List<CollectionDTO> findPublicId(String id) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaCollection> query = em.createQuery("SELECT c FROM collection c WHERE c.id = :id AND c.privateStatus = 0 ORDER BY c.name", JpaCollection.class);
            query.setParameter("id", id);
            List<JpaCollection> jpaList = query.getResultList();
            return jpaList.stream().map(JpaCollection::toDTO).collect(Collectors.toList());
        });
    }
    
    @Override
    public List<CollectionDTO> findPublic(){
    	return transactionControl.notSupported(() -> {
            TypedQuery<JpaCollection> query = em.createQuery("SELECT c FROM collection c WHERE c.privateStatus = 0 ORDER BY c.creation_date DESC", JpaCollection.class);
            query.setMaxResults(30);
            List<JpaCollection> jpaList = query.getResultList();
            return jpaList.stream().map(JpaCollection::toDTO).collect(Collectors.toList());
        });
    }

    @Override
    public void save(CollectionDTO collection) {
        transactionControl.required(() -> {
            JpaCollection jpaCol = JpaCollection.fromDTO(collection);

            // attach all repos, which already exist in the database to the current JPA persistence context
            attachRepositories(jpaCol);

            em.persist(jpaCol);
            return null;
        });
    }

    private void attachRepositories(JpaCollection collection) {
        for (JpaVersion version : collection.versions) {
            super.attachRepositories(version);
        }
    }

    @Override
    public void update(CollectionDTO collection) {
        transactionControl.required(() -> {
            JpaCollection jpaCollection = em.find(JpaCollection.class, collection.id);
            jpaCollection.name = collection.name;
            jpaCollection.description = collection.description;
            jpaCollection.privateStatus = (collection.privateStatus)? 1: 0;
            em.persist(jpaCollection);
            return null;
        });
    }

    @Override
    public void delete(String id) {
        transactionControl.required(() -> {
            JpaCollection collection = em.find(JpaCollection.class, id);
            em.remove(collection);
            Query deleteOrphanProperties = em.createNativeQuery("delete from repository_property where repository_property.repository_id not in (select distinct repository_id from commit)");
            deleteOrphanProperties.executeUpdate();
            Query deleteOrphanRepos = em.createNativeQuery("delete from repository where repository.id not in (select distinct repository_id from commit)");
            deleteOrphanRepos.executeUpdate();
            return null;
        });
    }

    @Override
    public CollectionDTO findByCommit(String id) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaCollection> query = em.createQuery("select c from collection c join c.versions as v join v.commits as cmt where cmt.id = :id", JpaCollection.class);
            query.setParameter("id", id);
            JpaCollection result = query.getSingleResult();
            return result.toDTO();
        });
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
