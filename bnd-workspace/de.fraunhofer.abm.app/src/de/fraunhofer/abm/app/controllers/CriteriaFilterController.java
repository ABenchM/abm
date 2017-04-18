package de.fraunhofer.abm.app.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.fraunhofer.abm.domain.RepositoryDTO;
import de.fraunhofer.abm.domain.RepositoryPropertyDTO;
import de.fraunhofer.abm.projectanalysis.ProjectAnalysis;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireWebServerExtender
@RequireConfigurerExtender
@Component(name="de.fraunhofer.abm.rest.criteria")
public class CriteriaFilterController implements REST {

    private static final String NOT_APPLICABLE = "n/a";

    @Reference
    private ProjectAnalysis projectAnalysis;

    interface CriteriaRequest extends RESTRequest {
        RequestDTO _body();
    }

    public static class RequestDTO {
        public CriteriaDTO criteria;
        public RepositoryDTO[] repos;
    }

    public static class CriteriaDTO {
        public String[] buildsystems;
        public String[] languages;
        public String[] licenses;
        public int[] sizes;
    }

    public List<RepositoryDTO> postCriteria(CriteriaRequest rr) throws ServletException {
        RequestDTO dto = rr._body();

        try {
            List<RepositoryPropertyDTO> result = new ArrayList<>();
            for (RepositoryDTO repo : dto.repos) {
                result.addAll(projectAnalysis.analyze(repo));
            }

            List<RepositoryDTO> matches = filterResultsWithCriteria(result, dto);
            return matches;
        } catch (Exception e) {
            throw new ServletException("Couldn't analyze repositories", e);
        }
    }

    private List<RepositoryDTO> filterResultsWithCriteria(List<RepositoryPropertyDTO> result, RequestDTO request) {
        List<RepositoryDTO> matchingRepos = new ArrayList<>();
        CriteriaDTO criteria = request.criteria;

        for (RepositoryDTO repositoryDTO : request.repos) {
            boolean matches = matchesBuildSystem(criteria, result, repositoryDTO);
            matches = matches & matchesLanguage(criteria, result, repositoryDTO);
            matches = matches & matchesLicense(criteria, result, repositoryDTO);
            matches = matches & matchesSize(criteria, result, repositoryDTO);
            if(matches) {
                matchingRepos.add(repositoryDTO);
            }
        }

        return matchingRepos;
    }

    private boolean matchesLanguage(CriteriaDTO criteria, List<RepositoryPropertyDTO> result, RepositoryDTO repositoryDTO) {
        boolean matches = criteria.languages.length == 0;
        for (String language : criteria.languages) {
            matches = matches | matchesStringCriteria(language, "language", result, repositoryDTO);
        }
        return matches;
    }

    private boolean matchesBuildSystem(CriteriaDTO criteria, List<RepositoryPropertyDTO> result, RepositoryDTO repositoryDTO) {
        boolean matches = criteria.buildsystems.length == 0;
        for (String buildsystem : criteria.buildsystems) {
            matches = matches | matchesStringCriteria(buildsystem, "build.system", result, repositoryDTO);
        }
        return matches;
    }

    private boolean matchesLicense(CriteriaDTO criteria, List<RepositoryPropertyDTO> result, RepositoryDTO repositoryDTO) {
        boolean matches = criteria.licenses.length == 0;
        for (String license : criteria.licenses) {
            matches = matches | matchesStringCriteria(license, "license", result, repositoryDTO);
        }
        return matches;
    }

    private boolean matchesStringCriteria(String specifiedValue, String criteriaName, List<RepositoryPropertyDTO> result, RepositoryDTO repositoryDTO) {
        boolean matches = true;
        if(specifiedValue != null) {
            String determinedValue = getProperty(result, repositoryDTO, criteriaName);
            if(!determinedValue.equals(specifiedValue) && !determinedValue.equals(NOT_APPLICABLE)) {
                matches = false;
            }
        }
        return matches;
    }

    private boolean matchesSize(CriteriaDTO criteria, List<RepositoryPropertyDTO> result, RepositoryDTO repositoryDTO) {
        String size = getProperty(result, repositoryDTO, "files");
        if(size.equals(NOT_APPLICABLE)) {
            // does not match, continue to next one
            return true;
        }

        boolean matches = criteria.sizes.length == 0;
        for (int range : criteria.sizes) {
            boolean currentRangeMatches = true;
            int numberOfFiles = Integer.parseInt(size);
            switch (range) {
            case 0:
                if(numberOfFiles < 0 || numberOfFiles > 100)
                    currentRangeMatches = false;
                break;
            case 1:
                if(numberOfFiles <= 100 || numberOfFiles > 1000)
                    currentRangeMatches = false;
                break;
            case 2:
                if(numberOfFiles <= 1000 || numberOfFiles > 10000)
                    currentRangeMatches = false;
                break;
            case 3:
                if(numberOfFiles <= 10000)
                    currentRangeMatches = false;
                break;
            }
            matches |= currentRangeMatches;
        }
        return matches;
    }

    private String getProperty(List<RepositoryPropertyDTO> result, RepositoryDTO repositoryDTO, String propertyName) {
        for (RepositoryPropertyDTO prop : result) {
            if(prop.repositoryId.equals(repositoryDTO.id)) {
                if(prop.name.equals(propertyName)) {
                    return prop.value;
                }
            }
        }
        return NOT_APPLICABLE;
    }
}
