package de.fraunhofer.abm.useradmin.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;
import org.osgi.service.useradmin.Role;

import de.fraunhofer.abm.useradmin.dao.RoleDao;
import de.fraunhofer.abm.useradmin.dto.RoleDTO;

@Component(name = "de.fraunhofer.abm.useradmin.role.dao", service = RoleDao.class, configurationPid = "de.fraunhofer.abm.useradmin.role.dao")
public class JdbcRoleDao implements RoleDao {

    @Reference
    TransactionControl transactionControl;

    public static final int TYPE_PROPERTY_STRING = 0;
    public static final int TYPE_PROPERTY_BINARY = 1;
    public static final int TYPE_CREDENTIAL_STRING = 2;
    public static final int TYPE_CREDENTIAL_BINARY = 3;

    @Reference(name="provider")
    JDBCConnectionProvider jdbcConnectionProvider;

    Connection connection;

    @Activate
    void activate(Map<String, Object> props) throws SQLException {
        connection = jdbcConnectionProvider.getResource(transactionControl);
    }

    @Deactivate
    void deactivate() throws SQLException {
        connection.close();
    }

    @Override
    public List<RoleDTO> select() {
        List<RoleDTO> roles = transactionControl.notSupported(() -> {

            List<RoleDTO> dbResults = new ArrayList<>();

            // naive implementation, if this causes performance problem, we have to reimplement it with
            // for example with joins or so
            ResultSet rs = connection.createStatement().executeQuery("SELECT `role_name` from `role`");
            while (rs.next()) {
                String role = rs.getString("role_name");
                dbResults.add(findByName(role));
            }

            return dbResults;
        });

        return roles;
    }

    @Override
    public RoleDTO findByName(String name) {
        RoleDTO role = transactionControl.notSupported(() -> {

            // load the role
            PreparedStatement pst = connection.prepareStatement("SELECT * from `role` WHERE `role_name` = ?");
            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();
            RoleDTO roleDTO = null;
            if (rs.next()) {
                roleDTO = mapRecordToRole(rs);
            } else {
                return null;
            }
            pst.close();

            // load role properties (and credentials, if role is a user)
            pst = connection.prepareStatement("SELECT * from `role_properties` WHERE `property_role` = ?");
            pst.setString(1, name);
            rs = pst.executeQuery();
            while (rs.next()) {
                mapRecordToPropertyOrCredential(rs, roleDTO);
            }
            pst.close();

            if (roleDTO.type == Role.GROUP) {
                pst = connection.prepareStatement("SELECT * from `role_members` WHERE `member_parent` = ?");
                pst.setString(1, name);
                rs = pst.executeQuery();
                while (rs.next()) {
                    String member = rs.getString("member_member");
                    boolean isBasic = rs.getBoolean("member_is_basic");
                    RoleDTO childDTO = findByName(member);
                    if (isBasic) {
                        roleDTO.basicMembers.add(childDTO);
                    } else {
                        roleDTO.requiredMembers.add(childDTO);
                    }
                }
                pst.close();
            }

            return roleDTO;
        });
        return role;
    }

    @Override
    public void save(RoleDTO data) {
        transactionControl.required(() -> {
            // save the role
            PreparedStatement pst = connection.prepareStatement("INSERT INTO role (role_name, role_type) VALUES(?,?)");
            pst.setString(1, data.name);
            pst.setInt(2, data.type);
            pst.executeUpdate();
            pst.close();

            // save the properties
            savePropertiesOrCredentials(data.name, data.properties, TYPE_PROPERTY_STRING, TYPE_PROPERTY_BINARY);

            // save the credentials
            if(data.type == Role.USER) {
                savePropertiesOrCredentials(data.name, data.credentials, TYPE_CREDENTIAL_STRING, TYPE_CREDENTIAL_BINARY);
            }

            // save the group members
            if(data.type == Role.GROUP) {
                for (RoleDTO member : data.basicMembers) {
                    save(member);
                    addMemberToRole(member.name, data.name, true);
                }
                for (RoleDTO member : data.requiredMembers) {
                    save(member);
                    addMemberToRole(member.name, data.name, false);
                }
            }

            return 0;
        });
    }

    @SuppressWarnings("rawtypes")
    private void savePropertiesOrCredentials(String role, Dictionary values, int typeString, int typeBinary) throws SQLException {
        Enumeration properties = values.keys();
        while(properties.hasMoreElements()) {
            String key = (String) properties.nextElement();
            Object value = values.get(key);
            boolean isString = value instanceof String;
            String stringValue = convertPropertyOrCredential(value);

            PreparedStatement pst = connection.prepareStatement("INSERT INTO role_properties (property_role, property_name, property_value, property_type) VALUES(?,?,?,?)");
            pst.setString(1, role);
            pst.setString(2, key);
            pst.setString(3, stringValue);
            pst.setInt(4, isString ? typeString : typeBinary);
            pst.executeUpdate();
            pst.close();
        }
    }

    private String convertPropertyOrCredential(Object value) {
        if(value instanceof String) {
            return (String)value;
        } else  if(value instanceof byte[]) {
            return Base64.getEncoder().encodeToString((byte[]) value);
        } else {
            throw new IllegalArgumentException("Property or Credential has to be a String or byte[]");
        }
    }

    @Override
    public void update(RoleDTO data) {
        /* Naive implementation for now
         * We delete all old values and create the new ones
         */
        transactionControl.required(() -> {
            // delete old properties and credentials
            PreparedStatement pst = connection.prepareStatement("DELETE FROM `role_properties` WHERE property_role=?");
            pst.setString(1, data.name);
            pst.executeUpdate();

            // save the new properties
            savePropertiesOrCredentials(data.name, data.properties, TYPE_PROPERTY_STRING, TYPE_PROPERTY_BINARY);

            // save the new credentials
            if(data.type == Role.USER) {
                savePropertiesOrCredentials(data.name, data.credentials, TYPE_CREDENTIAL_STRING, TYPE_CREDENTIAL_BINARY);
            }

            if(data.type == Role.GROUP) {
                // delete the old members
                pst = connection.prepareStatement("DELETE FROM `role_members` WHERE member_parent=?");
                pst.setString(1, data.name);
                pst.executeUpdate();

                // save the new members
                for (RoleDTO member : data.basicMembers) {
                    addMemberToRole(member.name, data.name, true);
                }
                for (RoleDTO member : data.requiredMembers) {
                    addMemberToRole(member.name, data.name, false);
                }
            }

            return 0;
        });
    }

    private void addMemberToRole(String member, String parent, boolean isBasic) throws SQLException {
        PreparedStatement pst = connection.prepareStatement("INSERT INTO role_members (member_parent, member_member, member_is_basic) VALUES(?,?,?)");
        pst.setString(1, parent);
        pst.setString(2, member);
        pst.setBoolean(3, isBasic);
        pst.executeUpdate();
        pst.close();
    }

    @Override
    public void delete(String name) {
        transactionControl.required(() -> {
            int affectedRows = 0;

            // delete role from groups
            PreparedStatement pst = connection.prepareStatement("DELETE FROM `role_members` WHERE member_member=?");
            pst.setString(1, name);
            affectedRows += pst.executeUpdate();

            // delete members referencing this role (if role is a group)
            pst = connection.prepareStatement("DELETE FROM `role_members` WHERE member_parent=?");
            pst.setString(1, name);
            affectedRows += pst.executeUpdate();

            // delete properties and credentials
            pst = connection.prepareStatement("DELETE FROM `role_properties` WHERE property_role=?");
            pst.setString(1, name);
            affectedRows += pst.executeUpdate();

            // delete the role
            pst = connection.prepareStatement("DELETE FROM `role` WHERE role_name=?");
            pst.setString(1, name);
            affectedRows += pst.executeUpdate();

            return affectedRows;
        });
    }

    private RoleDTO mapRecordToRole(ResultSet rs) throws Exception {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.name = rs.getString("role_name");
        roleDTO.type = rs.getInt("role_type");
        return roleDTO;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void mapRecordToPropertyOrCredential(ResultSet rs, RoleDTO roleDTO) throws SQLException {
        int type = rs.getInt("property_type");
        Dictionary target;
        if(type == TYPE_PROPERTY_STRING || type == TYPE_PROPERTY_BINARY) {
            target = roleDTO.properties;
        } else {
            target = roleDTO.credentials;
        }

        String name = rs.getString("property_name");
        boolean isString = (type == TYPE_PROPERTY_STRING || type == TYPE_CREDENTIAL_STRING);
        Object value = isString ? getString(rs, "property_value") : getByteArray(rs, "property_value");
        target.put(name, value);
    }

    private Object getByteArray(ResultSet rs, String column) throws SQLException {
        String bytes = rs.getString(column);
        //Decode encoded salt
        String salt=bytes.split("\\$")[0];
        return Base64.getDecoder().decode(salt);
    }

    private Object getString(ResultSet rs, String column) throws SQLException {
        return rs.getString(column);
    }
}
