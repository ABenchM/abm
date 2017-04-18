package de.fraunhofer.abm.collection.dao.jpa;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.CommitDao;
import de.fraunhofer.abm.domain.CommitDTO;

@Component
public class JpaCommitDao implements CommitDao {

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
    public CommitDTO findById(String id) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaCommit> query = em.createQuery("SELECT c FROM commit c WHERE c.id = :id", JpaCommit.class);
            query.setParameter("id", id);
            JpaCommit result = query.getSingleResult();
            return result.toDTO();
        });
    }

    @Override
    public void delete(String id) {
        transactionControl.required(() -> {
            JpaCommit commit = em.find(JpaCommit.class, id);
            em.remove(commit);
            Query deleteOrphanProperties = em.createNativeQuery("delete from repository_property where repository_property.repository_id not in (select distinct repository_id from commit)");
            deleteOrphanProperties.executeUpdate();
            Query deleteOrphanRepos = em.createNativeQuery("delete from repository where repository.id not in (select distinct repository_id from commit)");
            deleteOrphanRepos.executeUpdate();
            return null;
        });
    }

    @Override
    public void update(CommitDTO commit) {
        transactionControl.required(() -> {
            JpaCommit jpaCommit = em.find(JpaCommit.class, commit.id);
            jpaCommit.commit = commit.commitId;
            jpaCommit.creationDate = commit.creationDate;
            jpaCommit.message = commit.message;
            em.persist(jpaCommit);
            return null;
        });
    }

}
