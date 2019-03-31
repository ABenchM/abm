package de.fraunhofer.abm.zenodo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by benhermann on 31.05.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Deposition {
    public Date created;
    public Integer id;
    public Date modified;
    public Integer owner;
    public Integer record_id;
    public String state;
    public String title;

    public Links links;
    public Metadata metadata;

    /**
     * Created by benhermann on 31.05.17.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {
        public String bucket;
        public String discard;
        public String edit;
        public String files;
        public String html;
        public String latest_draft;
        public String latest_draft_html;
        public String publish;
        public String self;
    }
}