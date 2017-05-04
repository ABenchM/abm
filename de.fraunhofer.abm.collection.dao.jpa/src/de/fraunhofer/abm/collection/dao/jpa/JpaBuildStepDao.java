package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Map;

import javax.persistence.EntityManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.BuildStepDao;
import de.fraunhofer.abm.domain.BuildStepDTO;

@Component
public class JpaBuildStepDao implements BuildStepDao {

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
    public BuildStepDTO findByVersion(String id) {
        return transactionControl.notSupported(() -> {
            JpaBuildStep result = em.find(JpaBuildStep.class, id);
            return result.toDTO();
        });
    }

    @Override
    public void save(BuildStepDTO buildStep) {
        transactionControl.required(() -> {
            JpaBuildStep jpaBuildStep = JpaBuildStep.fromDTO(buildStep);
            em.persist(jpaBuildStep);
            return null;
        });
    }

    @Override
    public void update(BuildStepDTO buildStep) {
        transactionControl.required(() -> {
            em.merge(JpaBuildStep.fromDTO(buildStep));
            return 0;
        });
    }

    @Override
    public void delete(String id) {
        transactionControl.required(() -> {
            JpaBuildStep buildStep = em.find(JpaBuildStep.class, id);
            em.remove(buildStep);
            return 0;
        });
    }

}
