package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.VersionDao;
import de.fraunhofer.abm.domain.CommitDTO;
import de.fraunhofer.abm.domain.ProjectObjectDTO;
import de.fraunhofer.abm.domain.VersionDTO;

@Component
public class JpaVersionDao extends AbstractJpaDao implements VersionDao {

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
    public VersionDTO findById(String id) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaVersion> query = em.createQuery("SELECT v FROM version v WHERE v.id = :id", JpaVersion.class);
            query.setParameter("id", id);
            JpaVersion result = query.getSingleResult();
            return result.toDTO();
        });
    }

    @Override
    public void save(VersionDTO version) {
        transactionControl.required(() -> {
            JpaVersion jpaVers = JpaVersion.fromDTO(version);
            jpaVers.collection = em.find(JpaCollection.class, version.collectionId);

            // attach all repos, which already exist in the database to the current JPA persistence context
            attachRepositories(jpaVers);

            em.persist(jpaVers);
            return null;
        });
    }

    @Override
    public void update(VersionDTO version) {
        transactionControl.required(() -> {
            JpaVersion jpaVersion = em.find(JpaVersion.class, version.id);
            jpaVersion.collection = em.find(JpaCollection.class, version.collectionId);
            jpaVersion.comment = version.comment;
            jpaVersion.number = version.number;
            jpaVersion.creationDate = version.creationDate;
            jpaVersion.frozen = version.frozen;
            jpaVersion.privateStatus = version.privateStatus;
            jpaVersion.filtered = version.filtered;
        	jpaVersion.name = version.name;
        	jpaVersion.derivedFrom = version.derivedFrom;

            Map<String, ProjectObjectDTO> projects = version.projects.stream()
                    .collect(Collectors.toMap(c -> c.id, Function.identity()));

            // update existing commits and remove deleted ones
            jpaVersion.projects.forEach(c -> {
                ProjectObjectDTO dto = projects.remove(c.id);
                if(dto != null) {
                    c.version.id = dto.version_id;
                } else {
                    em.remove(c);
                }
            });
            em.flush();

            // add new commits
            projects.values().forEach(c -> {
            	JpaProject jpaProj = JpaProject.fromDTO(c);
                jpaProj.version = jpaVersion;
                //jpaCommit.repository = JpaRepository.fromDTO(c.repository);
                //attachRepository(jpaCommit);
                em.persist(jpaProj);
            });

            return null;
        });
    }

    @Override
    public void delete(String id) {
        transactionControl.required(() -> {
            JpaVersion version = em.find(JpaVersion.class, id);
            em.remove(version);
            Query deleteOrphanProperties = em.createNativeQuery("delete from repository_property where repository_property.repository_id not in (select distinct repository_id from commit)");
            deleteOrphanProperties.executeUpdate();
            Query deleteOrphanRepos = em.createNativeQuery("delete from repository where repository.id not in (select distinct repository_id from commit)");
            deleteOrphanRepos.executeUpdate();
            return null;
        });
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
