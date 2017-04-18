package de.fraunhofer.abm.collection.dao.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import de.fraunhofer.abm.domain.RepositoryDTO;

@Entity(name="repository")
public class JpaRepository {

    @Id
    @Column
    public String id;

    @Column(name = "remote_id")
    public int remoteId;

    @Column(nullable=true)
    public int forks;

    @Column(name = "open_issues")
    public int openIssues;
    /**
     * Repository size in KiB
     */
    @Column
    public int size;

    @Column(nullable=false)
    public String name;

    @Column
    public String description = "";

    @Column(name = "creation_date")
    public Date creationDate;

    @Column(name = "latest_update")
    public String latestUpdate = "";

    @Column
    public int score;

    @Column(name = "has_downloads")
    public boolean hasDownloads;

    @Column(name = "has_wiki")
    public boolean hasWiki;

    @Column(name = "is_private")
    public boolean isPrivate;

    @Column(name = "html_url")
    public String htmlUrl;

    @Column(name = "releases_url")
    public String releasesUrl;

    @Column(name = "issues_url")
    public String issuesUrl;

    @Column(name = "commits_url")
    public String commitsUrl;

    @Column(name = "contents_url")
    public String contentsUrl;

    @Column(name = "contributors_url")
    public String contributorsUrl;

    @Column(name = "repository_url")
    public String repositoryUrl;

    @Column(name = "repository_type")
    public String repositoryType;

    @Column(name = "default_branch")
    public String defaultBranch;

    @Column
    public String license = "";

    @Column
    public String owner = "";

    @Column(name = "owner_type")
    public String ownerType = "";

    @Column
    public int starred;

    @Column
    public int watched;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "repository")
    public List<JpaCommit> commits;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "repository")
    public List<JpaRepositoryProperty> properties;

    public static JpaRepository fromDTO(RepositoryDTO dto) {
        JpaRepository jpa = new JpaRepository();
        jpa.id = dto.id;
        jpa.remoteId = dto.remoteId;
        jpa.repositoryUrl = dto.repositoryUrl;
        jpa.repositoryType = dto.repositoryType;
        jpa.defaultBranch = dto.defaultBranch;
        jpa.commitsUrl = dto.commitsUrl;
        jpa.contentsUrl = dto.contentsUrl;
        jpa.contributorsUrl = dto.contributorsUrl;
        jpa.creationDate = dto.creationDate;
        jpa.description = dto.description;
        jpa.forks = dto.forks;
        jpa.hasDownloads = dto.hasDownloads;
        jpa.hasWiki = dto.hasWiki;
        jpa.htmlUrl = dto.htmlUrl;
        jpa.isPrivate = dto.isPrivate;
        jpa.issuesUrl = dto.issuesUrl;
        jpa.latestUpdate = dto.latestUpdate;
        jpa.license = dto.license;
        jpa.name = dto.name;
        jpa.openIssues = dto.openIssues;
        jpa.owner = dto.owner;
        jpa.ownerType = dto.ownerType;
        jpa.releasesUrl = dto.releasesUrl;
        jpa.score = dto.score;
        jpa.size = dto.size;
        jpa.starred = dto.starred;
        jpa.watched = dto.watched;
        jpa.properties = dto.properties.stream()
                .map(JpaRepositoryProperty::fromDTO)
                .map(prop -> {
                    prop.repository = jpa;
                    return prop;
                })
                .collect(Collectors.toList());
        return jpa;
    }

    public RepositoryDTO toDTO() {
        RepositoryDTO dto = new RepositoryDTO();
        dto.id = this.id;
        dto.remoteId = this.remoteId;
        dto.repositoryUrl = this.repositoryUrl;
        dto.repositoryType = this.repositoryType;
        dto.defaultBranch = this.defaultBranch;
        dto.commitsUrl = this.commitsUrl;
        dto.contentsUrl = this.contentsUrl;
        dto.contributorsUrl = this.contributorsUrl;
        dto.creationDate = this.creationDate;
        dto.description = this.description;
        dto.forks = this.forks;
        dto.hasDownloads = this.hasDownloads;
        dto.hasWiki = this.hasWiki;
        dto.htmlUrl = this.htmlUrl;
        dto.isPrivate = this.isPrivate;
        dto.issuesUrl = this.issuesUrl;
        dto.latestUpdate = this.latestUpdate;
        dto.license = this.license;
        dto.name = this.name;
        dto.openIssues = this.openIssues;
        dto.owner = this.owner;
        dto.ownerType = this.ownerType;
        dto.releasesUrl = this.releasesUrl;
        dto.score = this.score;
        dto.size = this.size;
        dto.starred = this.starred;
        dto.watched = this.watched;
        dto.commits = new ArrayList<>();
        this.commits.forEach(commit -> {
            dto.commits.add(commit.id);
        });
        dto.properties = new ArrayList<>();
        this.properties.forEach(prop -> {
            dto.properties.add(prop.toDTO());
        });
        return dto;
    }
}
