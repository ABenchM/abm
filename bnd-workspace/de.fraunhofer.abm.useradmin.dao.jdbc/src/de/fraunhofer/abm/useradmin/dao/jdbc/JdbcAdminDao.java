package de.fraunhofer.abm.useradmin.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;

import de.fraunhofer.abm.useradmin.dao.AdminDao;

@Component(name = "de.fraunhofer.abm.useradmin.admin.dao", service = AdminDao.class, configurationPid = "de.fraunhofer.abm.useradmin.admin.dao")
public class JdbcAdminDao implements AdminDao {
	
	 @Reference
	    TransactionControl transactionControl;
	 
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
		public void updateRole(String name, String rolename) {
			
		
			transactionControl.required(() -> {
			List<String> dbResults = new ArrayList<>();
			ResultSet rs = connection.createStatement().executeQuery("SELECT `role_name` from `role`");
            while (rs.next()) {
                String role = rs.getString("role_name");
                dbResults.add(role);
            }
            for(String username : dbResults ) {
            	if(username!=name) {
            		PreparedStatement pst = connection.prepareStatement("INSERT INTO `admin` VALUES(?,?)");
            		pst.setString(1, name);
            		pst.setString(2,"admin");
            		pst.executeUpdate();
                    pst.close();
            	}
            		
            	}
            return 0;
            	
            });
            
			
			
		}
	
	
}
