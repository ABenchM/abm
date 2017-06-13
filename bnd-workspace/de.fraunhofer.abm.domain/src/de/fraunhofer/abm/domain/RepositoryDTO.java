package de.fraunhofer.abm.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data for a GitHub repository
 *
 * @author Lisa Nguyen Quang Do
 * @author Henrik Niehaus
 */

public class RepositoryDTO {

    public static final String TYPE_CVS = "cvs";
    public static final String TYPE_GIT = "git";
    public static final String TYPE_HG = "hg";
    public static final String TYPE_SVN = "svn";

    public String id;
    public int remoteId;
    public int forks;
    public int openIssues;
    /**
     * Repository size in KiB
     */
    public int size;

    public String name;
    public String description = "";
    public Date creationDate;
    public String latestUpdate = "";
    public int score;

    public boolean hasDownloads;
    public boolean hasWiki;
    public boolean isPrivate;

    public String htmlUrl;
    public String releasesUrl;
    public String issuesUrl;
    public String commitsUrl;
    public String contentsUrl;
    public String contributorsUrl;
    /**
     * The URL to clone the project from
     */
    public String repositoryUrl;
    /**
     * @see {@link #TYPE_CVS}
     * @see {@link #TYPE_GIT}
     * @see {@link #TYPE_HG}
     * @see {@link #TYPE_SVN}
     */
    public String repositoryType;
    public String defaultBranch;

    public String license = "";

    public String owner = "";
    public String ownerType = "";

    public int starred;
    public int watched;

    public List<String> commits;
    public List<RepositoryPropertyDTO> properties = new ArrayList<>();
}
