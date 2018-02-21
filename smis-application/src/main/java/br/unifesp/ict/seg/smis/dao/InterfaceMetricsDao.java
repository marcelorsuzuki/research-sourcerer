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
	public Integer retriveEntityId(Integer interfaceMetricId) {
		
		String sql = "Select entity_id from interface_metrics " 
		           + "Where id = ? "
				   + "And project_type = 'CRAWLED'";
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, interfaceMetricId);
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
	 * @param interfaceMetricsId
	 * 
	 * @return Method informations
	 */
	public EntityInfo getEntityInfo(Integer interfaceMetricsId) {
		String sql = "Select project_name, fqn, params, return_type, project_type, entity_id From interface_metrics where id = ?";
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, interfaceMetricsId);
			
			ResultSet rs = stmt.executeQuery();
			
			EntityInfo entity = new EntityInfo();
			if (rs.next()) {
				entity.setProjectName(rs.getString("project_name"));
				entity.fillClassAndMethod(rs.getString("fqn"));
				entity.setParams(rs.getString("params"));
				entity.setReturnType("return_type");
				entity.setProjectType(rs.getString("project_type"));
				entity.setEntityId(rs.getInt("entity_id"));
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
	 * @param results Array containing in each position the result of one execution
	 * @param exec Array containing in each possition the syntaxe of one execution
	 */
	public void updateEntityExec(int entityId, int errExec, String[] results, String execs[]) {
		
		String sql = "Update interface_metrics "
				   + "set error = ?, "
				   + "result1 = ?, "
				   + "result2 = ?, "
				   + "result3 = ?, "
				   + "result4 = ?, "
				   + "exec1 = ?, "
				   + "exec2 = ?, "
				   + "exec3 = ?, "
				   + "exec4 = ? "
			   	   + "where entity_id = ?";
		
		Connection conn = ConnectionFactory.openConnection();
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, errExec);
			for (int i = 0; i < results.length; i++)
				stmt.setString(i + 2, results[i]);
			
			for (int i = 0; i < execs.length; i++)
				stmt.setString(i + 6, execs[i]);

			stmt.setInt(10, entityId);
			
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

		String sql = "Select return_type, error, result1, result2, result3, result4, exec1, exec2, exec3, exec4 From interface_metrics "
				   + "where id = ? "
				   + "and error is not NULL";
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, interfaceMetricsId);
			
			ResultSet rs = stmt.executeQuery();
			
			String[] results = null;
			if (rs.next()) {
				results = new String[10];
				results[0] = rs.getString("return_type");
				results[1] = rs.getString("error");
				results[2] = rs.getString("result1");
				results[3] = rs.getString("result2");
				results[4] = rs.getString("result3");
				results[5] = rs.getString("result4");
				results[6] = rs.getString("exec1");
				results[7] = rs.getString("exec2");
				results[8] = rs.getString("exec3");
				results[9] = rs.getString("exec4");
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
	 * @param typeCompare Tipo de comparação na busca
	 * 
	 * @return List with pair of id (entity_id and interface_metrics_id)
	 */
	public List<int[]> getSimilar(int entityId, String typeCompare) {

//		String sql = "SELECT interface_metrics_a, interface_metrics_b "
//				   + "FROM interface_metrics_pairs imp, interface_metrics im " 
//				   + "where im.entity_id = ? "
//				   + "and imp.interface_metrics_a = im.id "
//				   + "and imp.search_type = ?";
		
		String sql = "Select interface_metrics_a, interface_metrics_b "
				   + "From   interface_metrics_pairs p, interface_metrics a, interface_metrics b "
				   + "Where  a.id = p.interface_metrics_a "
				   + "And    b.id = p.interface_metrics_b "
				   + "And    a.entity_id = ? "
				   + "And    p.search_type = ? "
				   + "And    a.error is not null "
				   + "And    b.error is not null";		
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, entityId);
			stmt.setString(2, typeCompare);
			
			ResultSet rs = stmt.executeQuery();
			
			List<int[]> list = new ArrayList<>();
			
			while (rs.next()) {
				int[] results = new int[2];
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


	public Integer getErrorExecutedMethod(Integer entityId) {
		
		String sql = "Select error "
				   + "From interface_metrics "
				   + "where entity_id = ? ";
		
		Connection conn = ConnectionFactory.openConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, entityId);
			
			ResultSet rs = stmt.executeQuery();

			Integer error = null;
			if (rs.next()) {
				error = rs.getInt("error");
			}
			conn.close();
			return error;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
