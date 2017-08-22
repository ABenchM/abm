package de.fraunhofer.abm.collection.dao.jpa;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.HermesResultDao;
import de.fraunhofer.abm.domain.HermesResultDTO;

@Component
public class JpaHermesResultDao implements HermesResultDao {
	
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
	 public HermesResultDTO findById(String id) {
	        return transactionControl.notSupported(() -> {
	            JpaHermesResult result = em.find(JpaHermesResult.class, id);
	            return result.toDTO();
	        });
	    }
	
	 @Override
	    public HermesResultDTO findByVersion(String id) {
	        return transactionControl.notSupported(() -> {
	            TypedQuery<JpaHermesResult> query = em.createQuery("SELECT b FROM hermes_result b WHERE b.versionId = :version", JpaHermesResult.class);
	            query.setParameter("version", id);
	            try {
	            	JpaHermesResult result = query.getSingleResult();
	                return result.toDTO();
	            } catch(NoResultException e) {
	                return null;
	            }
	        });
	    }
	 
	 @Override
	    public List<HermesResultDTO> findRunning(String user) {
	        return transactionControl.notSupported(() -> {
	            TypedQuery<JpaHermesResult> query = em.createQuery("SELECT b FROM hermes_result b WHERE (b.status = 'RUNNING' OR b.status = 'WAITING') AND EXISTS "
	            		+ "(SELECT v FROM version v WHERE v.id = b.versionId AND EXISTS "
	            		+ "(SELECT c FROM collection c WHERE c.id = v.collection.id AND c.user = :user))", JpaHermesResult.class);
	            query.setParameter("user", user);
	            List<JpaHermesResult> jpaList = query.getResultList();
	            return jpaList.stream().map(JpaHermesResult::toDTO).collect(Collectors.toList());
	        });
	    }
	 
	 @Override
	    public void save(HermesResultDTO hermesResult) {
	        transactionControl.required(() -> {
	        	JpaHermesResult jpaHermesResult = JpaHermesResult.fromDTO(hermesResult);
	            em.persist(jpaHermesResult);
	            return null;
	        });
	    }

	    @Override
	    public void update(HermesResultDTO hermesResult) {
	        transactionControl.required(() -> {
	            em.merge(JpaHermesResult.fromDTO(hermesResult));
	            return 0;
	        });
	    }

	    @Override
	    public void delete(String id) {
	        transactionControl.required(() -> {
	        	JpaHermesResult hermesResult = em.find(JpaHermesResult.class, id);
	            em.remove(hermesResult);
	            return 0;
	        });
	    }

}
