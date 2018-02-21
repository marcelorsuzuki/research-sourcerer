package br.unifesp.ict.seg.smis.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class is used to handler "interface_metrics" table.
 * 
 * @author Marcelo Suzuki
 *
 */
public class ProjectDao {
	
	/**
	 * Get a project path from an entity id
	 *  
	 * @param entityId - Entity Id
	 * 
	 * @return Project path
	 */
	public String getPathByEntityId(Integer entityId) {
		
		String sql = "SELECT path FROM projects p, entities e "
				   + "where entity_id = ? "
				   + "and p.project_id = e.project_id;";
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, entityId);
			ResultSet rs = stmt.executeQuery();
			String path = "";
			if (rs.next()) {
				 path = rs.getString("path");
			}
			conn.close();
			return path;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
		
}
