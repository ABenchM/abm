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

import de.fraunhofer.abm.collection.dao.ProjectDao;
import de.fraunhofer.abm.domain.ProjectObjectDTO;

@Component
public class JpaProjectDao extends AbstractJpaDao implements ProjectDao{
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
	public void save(ProjectObjectDTO project) {
		// TODO Auto-generated method stub
		transactionControl.required(() -> {
            JpaProject jpaProj = JpaProject.fromDTO(project);
            System.out.println(jpaProj.project_id.toString());
            System.out.println(jpaProj.id.toString());            
            em.persist(jpaProj);
            return null;
        });
		
	}

	public List<ProjectObjectDTO> findproject(String projectId) {
		// TODO Auto-generated method stub
		return transactionControl.notSupported(() -> {
            TypedQuery<JpaProject> query = em.createQuery("SELECT c FROM project c WHERE c.id = :projectid", JpaProject.class);
            query.setParameter("projectid", projectId);
            query.setMaxResults(30);
            List<JpaProject> jpaList = query.getResultList();
            return jpaList.stream().map(JpaProject::toDTO).collect(Collectors.toList());
        });
	}

	@Override
	protected EntityManager getEntityManager() {
		// TODO Auto-generated method stub
		return em;
	}
	
	@Override
	public void delete(ProjectObjectDTO projectId) {
		transactionControl.required(() -> {
            JpaProject project = JpaProject.fromDTO(projectId);
            project = em.find(JpaProject.class, project.id);
			em.remove(project);
            return null;
        });
		
	}

}
