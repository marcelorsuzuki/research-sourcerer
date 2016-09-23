package br.unifesp.ict.seg.smis.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class EntityDao {
	
	public Integer retriveEntityId(String entityMetricId) {
		
		String sql = "Select entity_id from interface_metrics " +
					 "where id = ?";
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, entityMetricId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("entity_id");
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
