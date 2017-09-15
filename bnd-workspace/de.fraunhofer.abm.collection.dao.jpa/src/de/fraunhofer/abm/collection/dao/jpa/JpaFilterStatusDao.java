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

import de.fraunhofer.abm.collection.dao.FilterStatusDao;
import de.fraunhofer.abm.domain.FilterStatusDTO;

@Component
public class JpaFilterStatusDao implements FilterStatusDao {
	
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
    public List<FilterStatusDTO> findFilters(String versionId)
    {
    	return transactionControl.notSupported(() -> {
            TypedQuery<JpaFilterStatus> query = em.createQuery("SELECT b FROM filter_status b WHERE b.versionid = :versionId", JpaFilterStatus.class);
            query.setParameter("versionId", versionId);
            try {
            	List<JpaFilterStatus> resultList = query.getResultList();
                return resultList.stream().map(JpaFilterStatus::toDTO).collect(Collectors.toList());
            } catch(NoResultException e) {
                return null;
            }
        });
    }
    
    @Override
    public void addFilter(FilterStatusDTO dto)
    {
    	transactionControl.required(() -> {
			JpaFilterStatus jpafil = JpaFilterStatus.fromDTO(dto);
			em.persist(jpafil);
            return null;
        });
    }
    @Override
	public void dropFilters(String versionId) {
		transactionControl.required(() -> {
			Query oldFilters = em.createNativeQuery("DELETE FROM filter WHERE versionid = :versionId");
			oldFilters.setParameter("versionid", versionId);
			//oldFilters.setMaxResults(1);
			oldFilters.executeUpdate();
            return null;
        });

	}
    
}