package br.unifesp.ict.seg.smis.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.unifesp.ict.seg.smis.process.EntityInfo;


/**
 * This class is used to handler "interface_metrics" table.
 * 
 * @author Marcelo Suzuki
 *
 */
public class InterfaceMetricsDao {
	
	/**
	 * Get id of entity table by an id from interface_metrics table
	 *  
	 * @param interfaceMetricId - Interface Metric Id
	 * 
	 * @return Entity Id
	 */
	public Integer retriveEntityId(String interfaceMetricId) {
		
		String sql = "Select entity_id from interface_metrics " +
					 "where id = ?";
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, interfaceMetricId);
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
	
	
	/**
	 * Get information about one method
	 * 
	 * @param entityId Entity id
	 * 
	 * @return Method informations
	 */
	public EntityInfo getEntityInfo(String entityId) {
		String sql = "Select project_name, fqn, params, return_type From interface_metrics where entity_id=?";
		
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
	
	
	/**
	 * Update Interface Metrics with results of execution
	 * 
	 * @param entityId Entity Id
	 * @param errExec 0 - Indicates no error in execution
	 *                1 - Indicates that method wasn't executed
	 * @param result1 Result in first execution
	 * @param result2 Result in second execution
	 * @param result3 Result in third execution
	 */
	public void updateEntityExec(int entityId, int errExec, String result1, String result2, String result3) {
		
		String sql = "Update interface_metrics "
				   + "set error = ?, "
				   + "result1 = ?, "
				   + "result2 = ?, "
				   + "result3 = ? "
			   	   + "where entity_id = ?";
		
		Connection conn = ConnectionFactory.openConnection();
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, errExec);
			stmt.setString(2, result1);
			stmt.setString(3, result2);
			stmt.setString(4, result3);
			stmt.setInt(5, entityId);
			
			stmt.executeUpdate();
			
			stmt.close();
			conn.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get results from one method execution
	 * 
	 * @param interfaceMetricsId Interface Metrics id
	 * 
	 * @return Index 0: 0 - Indicates no error in execution 
	 *                  1 - Indicates error in execution
	 *                  
	 *         Index 1: Result in first execution
	 *         Index 2: Result in second execution
	 *         Index 1: Result in third execution
	 */
	public String[] getResults(int interfaceMetricsId) {

		String sql = "Select error, result1, result2, result3 From interface_metrics where id=?";
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, interfaceMetricsId);
			
			ResultSet rs = stmt.executeQuery();
			
			String[] results = new String[4];
			if (rs.next()) {
				results[0] = rs.getString("error");
				results[1] = rs.getString("result1");
				results[2] = rs.getString("result2");
				results[3] = rs.getString("result3");
			}
			conn.close();
			return results;
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;

	}


	/**
	 * Get a list of similar methods by interface
	 * 
	 * @param entityId Method id
	 * 
	 * @return List with pair of id (entity_id and interface_metrics_id)
	 */
	public List<int[]> getSimilar(int entityId) {

		String sql = "SELECT interface_metrics_a, interface_metrics_b "
				   + " FROM interface_metrics_pairs imp, interface_metrics im " 
				   + "where im.entity_id = ? "
				   + "and imp.interface_metrics_a = im.id "
				   + "and imp.search_type = 'p1_c1_w1_t1'";
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, entityId);
			
			ResultSet rs = stmt.executeQuery();
			
			List<int[]> list = new ArrayList<>();
			int[] results = new int[2];
			
			if (rs.next()) {
				results[0] = rs.getInt("interface_metrics_a");
				results[1] = rs.getInt("interface_metrics_b");
				list.add(results);
			}
			conn.close();
			return list;
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;

	}
	
}
