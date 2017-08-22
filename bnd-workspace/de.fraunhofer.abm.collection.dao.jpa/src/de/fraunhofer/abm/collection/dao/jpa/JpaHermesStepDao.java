package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Map;

import javax.persistence.EntityManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.HermesStepDao;
import de.fraunhofer.abm.domain.HermesStepDTO;

@Component
public class JpaHermesStepDao implements HermesStepDao {

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
	    public HermesStepDTO findByVersion(String id) {
	        return transactionControl.notSupported(() -> {
	            JpaHermesStep result = em.find(JpaHermesStep.class, id);
	            return result.toDTO();
	        });
	    }
	    
	    @Override
	    public void save(HermesStepDTO hermesStep) {
	        transactionControl.required(() -> {
	            JpaHermesStep jpaHermesStep = JpaHermesStep.fromDTO(hermesStep);
	            em.persist(jpaHermesStep);
	            return null;
	        });
	    }

	    @Override
	    public void update(HermesStepDTO hermesStep) {
	        transactionControl.required(() -> {
	            em.merge(JpaHermesStep.fromDTO(hermesStep));
	            return 0;
	        });
	    }

	    @Override
	    public void delete(String id) {
	        transactionControl.required(() -> {
	            JpaHermesStep hermesStep = em.find(JpaHermesStep.class, id);
	            em.remove(hermesStep);
	            return 0;
	        });
	    }
	    
	
}
