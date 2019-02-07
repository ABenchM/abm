package de.fraunhofer.abm.collection.dao.jpa;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
        	TypedQuery<JpaCollection> query = em.createQuery("SELECT c FROM collection c WHERE c.user = :user AND c.isActive = 1 ORDER BY c.name", JpaCollection.class);
            query.setParameter("user", user);
            List<JpaCollection> jpaList = query.getResultList();
            return jpaList.stream().map(JpaCollection::toDTO).collect(Collectors.toList());
        });
    }

    @Override
    public List<CollectionDTO> findCollections(){
    	//Collections returned for manage collections for admin
    	return transactionControl.notSupported(() -> {
            TypedQuery<JpaCollection> query = em.createQuery("SELECT distinct c FROM collection c join c.versions as v WHERE v.privateStatus = 0 ORDER BY c.creation_date ASC", JpaCollection.class);
            query.setMaxResults(100);
            List<JpaCollection> jpaList = query.getResultList();
            return jpaList.stream().map(JpaCollection::toDTO).collect(Collectors.toList());
        });
}
    @Override
    public CollectionDTO findById(String id) {
        return transactionControl.notSupported(() -> {
        	TypedQuery<JpaCollection> query = em.createQuery("SELECT c FROM collection c WHERE c.id = :id AND c.isActive = 1 ORDER BY c.name", JpaCollection.class);
            query.setParameter("id", id);
            try {
            	 JpaCollection result = query.getSingleResult();	
            	 return result.toDTO();
            } catch (NoResultException e) {
             return null;
            }
        });
    }
    @Override
    public List<CollectionDTO> findPublicId(String id) {
        return transactionControl.notSupported(() -> {
        	TypedQuery<JpaCollection> query = em.createQuery("SELECT c FROM collection c join c.versions as v WHERE c.id = :id AND v.privateStatus = 0 AND c.isActive = 1", JpaCollection.class);

            query.setParameter("id", id);
            List<JpaCollection> jpaList = query.getResultList();
            return jpaList.stream().map(JpaCollection::toDTO).collect(Collectors.toList());
        });
    }
    
    @Override
    public List<CollectionDTO> findPrivateId(String id, String user) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaCollection> query = em.createQuery("SELECT c FROM collection c WHERE c.id = :id AND c.privateStatus = 1 AND c.user = :user", JpaCollection.class);
            query.setParameter("id", id);
            query.setParameter("user", user);
            List<JpaCollection> jpaList = query.getResultList();
            return jpaList.stream().map(JpaCollection::toDTO).collect(Collectors.toList());
        });
    }
    
    @Override
    public List<CollectionDTO> findPublic(){
    	return transactionControl.notSupported(() -> {
            TypedQuery<JpaCollection> query = em.createQuery("SELECT distinct c FROM collection c join c.versions as v WHERE v.privateStatus = 0 AND c.isActive = 1 ORDER BY c.creation_date ASC", JpaCollection.class);
            query.setMaxResults(30);
            List<JpaCollection> jpaList = query.getResultList();
            return jpaList.stream().map(JpaCollection::toDTO).collect(Collectors.toList());
        });
    }
    @Override
    public List<CollectionDTO> findPublic(String keywords){
    	String[] keywordArray = keywords.split(" ");
    	String partialQuery = "SELECT c FROM collection c WHERE c.privateStatus = 0 AND (c.name LIKE :keyword0 OR c.description LIKE :keyword0 OR c.id = :id) AND c.isActive = 1";
    	for(int i=1;i<keywordArray.length;i++){
    		partialQuery = partialQuery + " AND (c.name LIKE :keyword"+i+" OR c.description LIKE :keyword"+i+")";
    	}
    	final String generatedQuery = partialQuery;
    	return transactionControl.notSupported(() -> {
            TypedQuery<JpaCollection> query = em.createQuery(generatedQuery, JpaCollection.class);
            query.setMaxResults(30);
            for(int i=0;i<keywordArray.length;i++){
        		query.setParameter("keyword" + i, "%" + keywordArray[i] + "%");
        	}
            query.setParameter("id", keywordArray[0]);
            List<JpaCollection> jpaList = query.getResultList();
            return jpaList.stream().map(JpaCollection::toDTO).collect(Collectors.toList());
        });
    }

    @Override
    public void save(CollectionDTO collection) {
        transactionControl.required(() -> {
            JpaCollection jpaCol = JpaCollection.fromDTO(collection);

            // attach all repos, which already exist in the database to the current JPA persistence context
            //attachRepositories(jpaCol);
            
            jpaCol.isActive = 1;
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
    public void deleteUserPinnedCollections(String user) {
    	// delete from CollectionPin & update Collection (privateStatus 0) user to demo
        transactionControl.required(() -> {
        	Query deleteOrphanProperties = em.createNativeQuery("delete from collectionPin where user = :value1").setParameter("value1", user);
        	deleteOrphanProperties.executeUpdate();
            return null;
        });
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void updateUserPublicCollections(String user) {
    	// delete from CollectionPin & update Collection (privateStatus 0) user to demo
        transactionControl.required(() -> {
        	List<String> collectionIds = em.createQuery("select id from collection where user = :value1 and privateStatus = 0")
                    									.setParameter("value1", user).getResultList();
        	for (String id : collectionIds) {
        		JpaCollection collection = em.find(JpaCollection.class, id);
                collection.user = "demo";
                em.persist(collection);
        	}
            return null;
        });
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void deleteUserPrivateCollections(String user) {
    	// delete from CollectionPin & delete Collection (privateStatus 1)
        transactionControl.required(() -> {
        	List<String> collectionIds = em.createQuery("select id from collection where user = :value1 and privateStatus = 1")
                    									.setParameter("value1", user).getResultList();
        	for (String id : collectionIds) {
        		JpaCollection collection = em.find(JpaCollection.class, id);
                em.remove(collection);
        	}
            return null;
        });
    }
    @Override
    public void activeCollection(String collectionid) {
		transactionControl.required(() -> {
			TypedQuery<JpaCollection> query = em.createQuery("SELECT c FROM collection c WHERE c.id = :id", JpaCollection.class);
			query.setParameter("id", collectionid);
			JpaCollection result = query.getSingleResult();
			if(result.isActive == 1) {
				result.isActive=0;
			}else {
				result.isActive=1;
			}
			em.merge(result);
			return null;
		});

}

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
