package de.fraunhofer.abm.zenodo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by benhermann on 01.06.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {

    Metadata() {

    }

    public Metadata(UploadType upload_type,
                    Date publication_date,
                    String title,
                    String description,
                    String version,
                    AccessRight accessRight) {
        this.upload_type  = upload_type.toString();
        this.publication_date = publication_date;
        this.title = title;
        this.description = description;
        this.version = version;
        this.access_right = accessRight.toString();
    }

    public PreserveDOI preserve_doi;
    public String upload_type;
    public String publication_type;
    public String image_type;
    public Date publication_date;
    public String title;
    // public ArrayList<Creator> creators;
    public String description;
    public String access_right;
    public String license;
    public Date embargo_date;
    public String access_conditions;
    public String doi;
    // public boolean preserve_doi;
    public String keywords;
    public String notes;
    // public ArrayList<Identifier> related_identifiers;
    // public ArrayList<Contributor> contributers;
    // public ArrayList<String> references;
    // public ArrayList<Community> communities;
    // public ArrayList<Grant> grants;
    public String journal_title;
    public String journal_volume;
    public String journal_issue;
    public String journal_pages;
    public String conference_title;
    public String conference_acronym;
    public String conference_dates;
    public String conference_place;
    public String conference_url;
    public String conference_session;
    public String conference_session_part;
    public String imprint_publisher;
    public String imprint_isbn;
    public String imprint_place;
    public String partof_title;
    public String partof_pages;
    public String version;
    public String thesis_supervisors;
    public String thesis_university;
    // public ArrayList<Subject> subjects;


    /**
     * Created by benhermann on 04.06.17.
     */
    public static class AccessRight {
        private String accessRight;

        private AccessRight(String accessRight) {
            this.accessRight = accessRight;
        }

        @Override
        public String toString() {
            return accessRight;
        }

        public static final AccessRight OPEN = new AccessRight("open");
        public static final AccessRight EMBARGOED = new AccessRight("embargoed");
        public static final AccessRight RESTRICTED = new AccessRight("restricted");
        public static final AccessRight CLOSED = new AccessRight("closed");
    }

    /**
     * Created by benhermann on 04.06.17.
     */
    public static class UploadType {
        private String uploadType;

        private UploadType(String uploadType) {
            this.uploadType = uploadType;
        }

        @Override
        public String toString() {
            return uploadType;
        }

        public static final UploadType PUBLICATION = new UploadType("publication");
        public static final UploadType POSTER = new UploadType("poster");
        public static final UploadType PRESENTATION = new UploadType("presentation");
        public static final UploadType DATASET = new UploadType("dataset");
        public static final UploadType IMAGE = new UploadType("image");
        public static final UploadType VIDEO = new UploadType("video");
        public static final UploadType SOFTWARE = new UploadType("software");


    }

    /**
     * Created by benhermann on 01.06.17.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PreserveDOI {

    }
}