package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Map;

import javax.persistence.EntityManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.HermesBuildDao;
import de.fraunhofer.abm.domain.HermesBuildDTO;
import de.fraunhofer.abm.domain.ProjectBuildDTO;

@Component
public class JpaHermesBuildDao implements HermesBuildDao {

	@Reference
	TransactionControl transactionControl;
	
	@Reference(name="provider")
	JPAEntityManagerProvider jpaEntityManagerProvider;

    EntityManager em;
    
    @Activate
    void start(Map<String, Object> props) {
        try {
            em = jpaEntityManagerProvider.getResource(transactionControl);
        } catch (Exception e) {
            e.printStackTrace();
        }}
   
        @Override
        public HermesBuildDTO findByVersion(String id) {
            return transactionControl.notSupported(() -> {
                JpaHermesBuild result = em.find(JpaHermesBuild.class, id);
                return result.toDTO();
            });
        }
        
        @Override
        public void save(HermesBuildDTO hermesBuild) {
            transactionControl.required(() -> {
                JpaHermesBuild jpaHermesBuild = JpaHermesBuild.fromDTO(hermesBuild);
                em.persist(jpaHermesBuild);
                return null;
            });
        }
        
        @Override
        public void update(HermesBuildDTO hermesBuild) {
            transactionControl.required(() -> {
                em.merge(JpaHermesBuild.fromDTO(hermesBuild));
                return 0;
            });
        }
        
        @Override
        public void delete(String id) {
            transactionControl.required(() -> {
            	JpaHermesBuild hermesBuild = em.find(JpaHermesBuild.class, id);
                em.remove(hermesBuild);
                return 0;
            });
        }
        
}
