package br.unifesp.ict.seg.smis.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * This class is used to handler "interface_metrics_compare" table.
 * 
 * @author Marcelo Suzuki
 *
 */
public class InterfaceMetricsPairsDao {
	
	
	/**
	 * Create an record in interface_metric_compare with two results comparasion.
	 */
	public void updateCompareResult(int interface_metrics_a, int interface_metrics_b, 
			                        short exec1, short exec2, short exec3, short exec4) {
		
		String sql = "Update interface_metrics_pairs "
				   + "set exec1 = ?, "
				   + "exec2 = ?, "
				   + "exec3 = ?, "
				   + "exec4 = ?, "
				   + "result = ? "
			   	   + "where interface_metrics_a = ? "
			   	   + "and interface_metrics_b = ? ";

		
		Connection conn = ConnectionFactory.openConnection();
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setShort(1, exec1);
			stmt.setShort(2, exec2);
			stmt.setShort(3, exec3);
			stmt.setShort(4, exec4);
			stmt.setInt(5, (exec1 + exec2 + exec3 + exec4));
			stmt.setInt(6, interface_metrics_a);
			stmt.setInt(7, interface_metrics_b);
			
			stmt.executeUpdate();
			
			stmt.close();
			conn.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
}
