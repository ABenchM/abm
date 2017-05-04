package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.BuildResultDao;
import de.fraunhofer.abm.domain.BuildResultDTO;

@Component
public class JpaBuildResultDao implements BuildResultDao {

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
    public BuildResultDTO findById(String id) {
        return transactionControl.notSupported(() -> {
            JpaBuildResult result = em.find(JpaBuildResult.class, id);
            return result.toDTO();
        });
    }

    @Override
    public BuildResultDTO findByVersion(String id) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaBuildResult> query = em.createQuery("SELECT b FROM build_result b WHERE b.versionId = :version", JpaBuildResult.class);
            query.setParameter("version", id);
            try {
                JpaBuildResult result = query.getSingleResult();
                return result.toDTO();
            } catch(NoResultException e) {
                return null;
            }
        });
    }

    @Override
    public void save(BuildResultDTO buildResult) {
        transactionControl.required(() -> {
            JpaBuildResult jpaBuildResult = JpaBuildResult.fromDTO(buildResult);
            em.persist(jpaBuildResult);
            return null;
        });
    }

    @Override
    public void update(BuildResultDTO buildResult) {
        transactionControl.required(() -> {
            em.merge(JpaBuildResult.fromDTO(buildResult));
            return 0;
        });
    }

    @Override
    public void delete(String id) {
        transactionControl.required(() -> {
            JpaBuildResult buildResult = em.find(JpaBuildResult.class, id);
            em.remove(buildResult);
            return 0;
        });
    }

}
