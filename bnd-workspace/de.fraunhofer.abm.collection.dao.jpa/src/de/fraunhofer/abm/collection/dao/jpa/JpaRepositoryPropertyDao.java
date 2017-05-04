package de.fraunhofer.abm.collection.dao.jpa;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProvider;

import de.fraunhofer.abm.collection.dao.RepositoryPropertyDao;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;

@Component
public class JpaRepositoryPropertyDao implements RepositoryPropertyDao {

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
        }
    }

    @Override
    public List<RepositoryPropertyDTO> findByRepository(String repositoryId) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaRepositoryProperty> query = em.createQuery("SELECT p FROM repository_property p WHERE p.repository.id = :repoId ORDER BY p.name", JpaRepositoryProperty.class);
            query.setParameter("repoId", repositoryId);
            List<JpaRepositoryProperty> jpaList = query.getResultList();
            List<RepositoryPropertyDTO> dtoList = jpaList.stream().map(JpaRepositoryProperty::toDTO).collect(Collectors.toList());
            dtoList.stream().forEach(prop -> { prop.repositoryId = repositoryId; });
            return dtoList;
        });
    }

    @Override
    public RepositoryPropertyDTO findById(String id) {
        return transactionControl.notSupported(() -> {
            TypedQuery<JpaRepositoryProperty> query = em.createQuery("SELECT p FROM repository_property p WHERE p.id = :id", JpaRepositoryProperty.class);
            query.setParameter("id", id);
            JpaRepositoryProperty result = query.getSingleResult();
            return result.toDTO();
        });
    }

    @Override
    public void save(RepositoryPropertyDTO property) {
        transactionControl.required(() -> {
            JpaRepository jpaRepo = em.find(JpaRepository.class, property.repositoryId);
            JpaRepositoryProperty jpaProp = JpaRepositoryProperty.fromDTO(property);
            jpaProp.repository = jpaRepo;
            em.persist(jpaProp);
            return 0;
        });
    }

    @Override
    public void update(RepositoryPropertyDTO property) {
        transactionControl.required(() -> {
            JpaRepository jpaRepo = em.find(JpaRepository.class, property.repositoryId);
            JpaRepositoryProperty jpaProp = JpaRepositoryProperty.fromDTO(property);
            jpaProp.repository = jpaRepo;
            em.merge(jpaProp);
            return 0;
        });
    }

    @Override
    public void delete(String id) {
        transactionControl.required(() -> {
            JpaRepositoryProperty collection = em.find(JpaRepositoryProperty.class, id);
            em.remove(collection);
            return 0;
        });
    }
}
