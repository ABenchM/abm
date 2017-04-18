package de.fraunhofer.abm.useradmin.dto;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class RoleDTO {

    /**
     * Role=0, User=1, Group=2
     * {@link https://osgi.org/javadoc/r6/cmpn/org/osgi/service/useradmin/Role.html#getType()}
     */
    public int type;
    public String name;

    @SuppressWarnings("rawtypes")
    public Dictionary properties = new Hashtable<>();

    @SuppressWarnings("rawtypes")
    public Dictionary credentials = new Hashtable<>();

    public List<RoleDTO> basicMembers = new ArrayList<>();
    public List<RoleDTO> requiredMembers = new ArrayList<>();
}
