package br.unifesp.ict.seg.smis.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import br.unifesp.ict.seg.smis.dao.InterfaceMetricsPairsDao;
import br.unifesp.ict.seg.smis.dao.InterfaceMetricsDao;

public class JarsCompare {

	private static final String TYPE_COMPARE[] = {"p0_c0_w0_t0",
												  "p0_c0_w0_t1",
												  "p0_c0_w1_t0",
												  "p0_c0_w1_t1",
												  "p0_c1_w0_t0",
												  "p0_c1_w0_t1",
												  "p0_c1_w1_t0",
												  "p0_c1_w1_t1",
												  "p1_c0_w0_t0",
												  "p1_c0_w0_t1",
												  "p1_c0_w1_t0",
												  "p1_c0_w1_t1",
												  "p1_c1_w0_t0",
												  "p1_c1_w0_t1",
												  "p1_c1_w1_t0",
												  "p1_c1_w1_t1"};

	/**
	 * This method compare the result of two jars. 
	 * 
	 */
	public void compareAll() {
		
		for (String typeCompare : TYPE_COMPARE) {
			
			//Get informations about settings
			Properties prop = new Properties();
			InputStream input;
			try {
				input = new FileInputStream("smis.properties");
				prop.load(input);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			//Get all files into the "jar-methods" directory
			String jarMethods = prop.getProperty("exec-folder") + "jar-methods";
			
			File folder = new File(jarMethods);
			File[] files = folder.listFiles();
	
			//Get all pairs 
			InterfaceMetricsDao dao = new InterfaceMetricsDao();
			List<int[]> idPairs = new ArrayList<>();		
			for (File f : files) {
				int entityId = Integer.parseInt(f.getName().substring(0, f.getName().length() - 4));
				List<int[]> tmp = dao.getSimilar(entityId, typeCompare);
				if (tmp != null && tmp.size() > 0) 
					idPairs.addAll(tmp);
			}
			
			InterfaceMetricsPairsDao daoCompare = new InterfaceMetricsPairsDao();
			
			for (int[] pair : idPairs) {
				String[] resultsMethod1 = dao.getResults(pair[0]);
				String[] resultsMethod2 = dao.getResults(pair[1]);
				
				//Error executing query
				if (resultsMethod1 == null || resultsMethod2 == null) {
					System.out.println(MessageFormat.format("Get Result error: {0, number, #} and {1, number, #}", pair[0], pair[1]));
					continue;
				}
				
				short[] execs = new short[5];
				
				String type1 = resultsMethod1[0];
				String type2 = resultsMethod2[0];
				execs[1] = compareExec(type1, resultsMethod1[2], type2, resultsMethod2[2]); 
				execs[2] = compareExec(type1, resultsMethod1[3], type2, resultsMethod2[3]);
				execs[3] = compareExec(type1, resultsMethod1[4], type2, resultsMethod2[4]);
				execs[4] = compareExec(type1, resultsMethod1[5], type2, resultsMethod2[5]);
				
				daoCompare.updateCompareResult(pair[0], pair[1], execs[1], execs[2], execs[3], execs[4]);
				
				int count = 0;
				for (int i = 1; i <= 4; i++)
					count += execs[i];
				
				String message = MessageFormat.format("{0, number, #};{1, number, #};{2};{3};{4};{5};{6, number, #.##%};{7}", 
													  pair[0], pair[1], execs[1], execs[2], execs[3], execs[4], count/3.0, typeCompare);
				System.out.println(message);
			}			
		}

	}
	
	
	/**
	 * Compare executions results and indicates if are similar or not
	 * 
	 * @param type1 Type of result1 
	 * @param result1 Result of first method
	 * @param type2 Type of result2 
	 * @param result2 Result of second method
	 * 
	 * @return 1 - Similar
	 *         0 - Not similar
	 */
	private short compareExec(String type1, String result1, String type2, String result2) {

		boolean res = false;
		
		if ((result1 == null) && (result2 == null))
			return 1;

		if ((result1 == null) || (result2 == null))
			return 0;
		
		if (result1.startsWith("Exception:")) {
			if (result2.startsWith("Exception:")) {
				return 1;
			}
			else 
				return 0;
		}
		
		if (result2.startsWith("Exception:")) {
			if (result1.startsWith("Exception:")) {
				return 1;
			}
			else 
				return 0;
		}


		if (result1.startsWith("Timeout")) {
			if (result2.startsWith("Timeout")) {
				return 1;
			}
			else 
				return 0;
		}
		
		if (result2.startsWith("Timeout")) {
			if (result1.startsWith("Timeout")) {
				return 1;
			}
			else 
				return 0;
		}
		
		try {
			if (type1.toLowerCase().contains("boolean")) {
				boolean r1 = Boolean.parseBoolean(result1);
				boolean r2 = Boolean.parseBoolean(result2);
				res = r1 == r2;
			} 
			else if (type1.toLowerCase().contains("char")) {
				char r1 = result1.toCharArray()[0];
				char r2 = result2.toCharArray()[0];
				res = r1 == r2;
			} 
			else if (type1.toLowerCase().contains("byte")) {
				byte r1 = Byte.parseByte(result1);
				byte r2 = Byte.parseByte(result2);
				res = r1 == r2;
			} 
			else if (type1.toLowerCase().contains("short") ||
					 type1.toLowerCase().contains("int")   ||
					 type1.toLowerCase().contains("long")  ||
					 type1.toLowerCase().contains("float") ||
					 type1.toLowerCase().contains("double")) {
				 	double r1 = Double.parseDouble(result1.replace(",", "."));
					double r2 = Double.parseDouble(result2.replace(",", "."));
					res = Math.abs(r1 - r2) < 0.000001;			
			 } 
//			else if (type1.toLowerCase().contains("int")) {
//				int r1 = Integer.parseInt(result1);
//				int r2 = Integer.parseInt(result2);
//				res = r1 == r2;
//			} 
//			else if (type1.toLowerCase().contains("long")) {
//				long r1 = Long.parseLong(result1);
//				long r2 = Long.parseLong(result2);
//				res = r1 == r2;
//			} 
//			else if (type1.toLowerCase().contains("float")) {
//				float r1 = Float.parseFloat(result1.replace(",", "."));
//				float r2 = Float.parseFloat(result2.replace(",", "."));
//				res = Math.abs(r1 - r2) < 0.000001;
//			} 
//			else if (type1.toLowerCase().contains("double")) {
//				double r1 = Double.parseDouble(result1.replace(",", "."));
//				double r2 = Double.parseDouble(result2.replace(",", "."));
//				res = Math.abs(r1 - r2) < 0.000001;
//			} 
			else if (type1.toLowerCase().contains("string")) {
				res = result1.equals(result2);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (res)
			return 1;
		else 
			return 0;
	}
}
