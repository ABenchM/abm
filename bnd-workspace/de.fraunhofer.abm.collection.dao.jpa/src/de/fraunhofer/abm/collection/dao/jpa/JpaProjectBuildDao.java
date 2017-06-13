package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Map;

import javax.persistence.EntityManager;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.ProjectBuildDao;
import de.fraunhofer.abm.domain.ProjectBuildDTO;

@Component
public class JpaProjectBuildDao implements ProjectBuildDao {

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
    public ProjectBuildDTO findByVersion(String id) {
        return transactionControl.notSupported(() -> {
            JpaProjectBuild result = em.find(JpaProjectBuild.class, id);
            return result.toDTO();
        });
    }

    @Override
    public void save(ProjectBuildDTO projectBuild) {
        transactionControl.required(() -> {
            JpaProjectBuild jpaProjectBuild = JpaProjectBuild.fromDTO(projectBuild);
            em.persist(jpaProjectBuild);
            return null;
        });
    }

    @Override
    public void update(ProjectBuildDTO projectBuild) {
        transactionControl.required(() -> {
            em.merge(JpaProjectBuild.fromDTO(projectBuild));
            return 0;
        });
    }

    @Override
    public void delete(String id) {
        transactionControl.required(() -> {
            JpaProjectBuild projectBuild = em.find(JpaProjectBuild.class, id);
            em.remove(projectBuild);
            return 0;
        });
    }

}
