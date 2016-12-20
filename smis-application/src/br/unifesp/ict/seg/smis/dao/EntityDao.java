package br.unifesp.ict.seg.smis.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.unifesp.ict.seg.smis.process.EntityInfo;


public class EntityDao {
	
	public Integer retriveEntityId(String entityMetricId) {
		
		String sql = "Select entity_id from interface_metrics " +
					 "where id = ?";
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, entityMetricId);
			ResultSet rs = stmt.executeQuery();
			Integer entityId = null;
			if (rs.next()) {
				 entityId = rs.getInt("entity_id");
			}
			conn.close();
			return entityId;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public EntityInfo getEntityInfo(String entityId) {
		String sql = "Select project_name, fqn, params, return_type From sourcererdb110.interface_metrics where entity_id=?";
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, entityId);
			
			ResultSet rs = stmt.executeQuery();
			
			EntityInfo entity = new EntityInfo();
			if (rs.next()) {
				entity.setProjectName(rs.getString("project_name"));
				entity.fillClassAndMethod(rs.getString("fqn"));
				entity.splitParams(rs.getString("params"));
				entity.setReturnType("return_type");
			}
			conn.close();
			return entity;
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

}
