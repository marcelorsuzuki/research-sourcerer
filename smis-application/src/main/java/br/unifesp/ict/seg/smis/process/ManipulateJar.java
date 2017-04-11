package br.unifesp.ict.seg.smis.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;

import br.unifesp.ict.seg.smis.dao.InterfaceMetricsDao;


/**
 * This class use the jars of a directory and execute them.
 * 
 * @author Marcelo Suzuki
 *
 */
/**
 * @author msuzuki
 *
 */
public class ManipulateJar {
	
	private final boolean createNewResult = false;
	//12871081, 12871082, 12871083, 12871084, 12871085, 12943008, 12947110, 13054554, 13054555, 13099404, 13099814, 13100428, 13100429
	//13099404, 13099814
	private final int lastIdProcessed = 13099814;
	

	private final boolean[]  booleanValues = {true, true, false};
	private final char[] charValues = {'m', 'K', ' '};
	private final byte[] byteValues = {'9', '\n', '\0'};
	private final short[] shortValues = {25, 0, -32};
	private final int[] intValues = {7, 0, -3};
	private final long[] longValues = {9, 0, -6};
	private final float[] floatValues = {3.2f, 0.0f, -9.8f};
	private final double[] doubleValues = {2.3f, 0.0f, -8.9f};
	private final String[] stringValues = {"Remove leading # and set to the amount of RAM for the most important data "+
	                                       "cache in MySQL. Start at 70% of total RAM for dedicated server, else 10%.", "", null};
	
	private String resultFilename;
	
	
	/**
	 * This class execute all jars of a directory
	 * 
	 */
	public void executeAllJars() {
		
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
		
		
		resultFilename = prop.getProperty("result-file");
		
		if (createNewResult) {
			try {
				File resultFile = new File(resultFilename);
				BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
				writer.write("project_id, entity_id, exec, exception"); 
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			

		//Get all files into the "jar-methods" directory
		String jarMethods = prop.getProperty("jar-methods");
		
		File folder = new File(jarMethods);
		File[] files = folder.listFiles();
		
		for (File f : files) {
			int id = Integer.parseInt(f.getName().substring(0, f.getName().length() - 4));
			if (id > lastIdProcessed) {
				System.out.println(f.getName());
				executeJar(f);
			}
		}

	}
	
	
	/**
	 * This method execute a method into a specific jar
	 *  
	 * @param file Jar file
	 * @throws InterruptedException 
	 */
	public void executeJar(File file)  {
		
		String SEPARATOR = ",";
		
		//Get in the database the method name by the id.
		String tmp = file.getName().substring(0, file.getName().length() - 4);
		Integer entityId = Integer.parseInt(tmp); 
		URL[] myJars = new URL[1];
		InterfaceMetricsDao dao = new InterfaceMetricsDao();
		EntityInfo entityInfo = dao.getEntityInfo(entityId.toString());
		
		Class<?> myClass = null;
		Method method = null;

		try {
			myJars[0] = file.toURI().toURL();
			
			//Load the jar file on classpath
			URLClassLoader child = new URLClassLoader(myJars, this.getClass().getClassLoader());
			
			//Get the class that is owner method 
			myClass = Class.forName(entityInfo.getClassName(), true, child);

			//Get the type of params used on method
			Class<?>[] params = new Class<?>[entityInfo.getParamList().size()];
			for (int i = 0; i < entityInfo.getParamList().size(); i++) {
				params[i] = getClassByName(entityInfo.getParamList().get(i));
			}
			
			//Get the method to be executed
			method = myClass.getMethod(entityInfo.getMethodName(), params);
		}
		catch (MissingResourceException | MalformedURLException | NoSuchMethodException | SecurityException | ClassNotFoundException e){
			//String message = MessageFormat.format("{1}{0} {2}{0} {3}{0} {4}", SEPARATOR, entityInfo.getProjectName(), entityId, -1, e);
			//writeResult(message);
			dao.updateEntityExec(entityId, 1, e.toString(), null, null);
			return;
		}
		catch (Error err) {
			dao.updateEntityExec(entityId, 1, err.toString(), null, null);
			System.out.println(err);
			return;
		}

		String [] results = new String[3];
		
		//Execute the method 3 times, with 3 differents inputs 
		for (int exec = 0; exec < 3; exec++) {
			
			String message;
			try {
				
				//Get the values defined to be used as input. The value used depends of param type and execution time
				Object[] values = new Object[entityInfo.getParamList().size()];
				for (int i = 0; i < entityInfo.getParamList().size(); i++) {
					values[i] = getValueByClass(entityInfo.getParamList().get(i), exec);
				}
				
				//The method is executed by a thread to have a timeout implemented
				ThreadExecute threadExec = new ThreadExecute(myClass, values, method);
				Thread thread = new Thread(threadExec);
				thread.start();
				thread.join(10000);

				if (thread.isAlive()) {
					//message = MessageFormat.format("{1}{0} {2}{0} {3}{0} {4}", SEPARATOR, entityInfo.getProjectName(), entityId, (exec + 1), "Timeout Exception");
					results[exec] = "Timeout Exception";
					thread.interrupt();
				}
				else {
					//message = MessageFormat.format("{1}{0} {2}{0} {3}{0} {4}", SEPARATOR, entityInfo.getProjectName(), entityId, (exec + 1), threadExec.getObj());
					results[exec] = MessageFormat.format("{0}", threadExec.getObj());
				}
			} catch (Exception e) {
				//message = MessageFormat.format("{1}{0} {2}{0} {3}{0} {4}", SEPARATOR, entityInfo.getProjectName(), entityId, (exec + 1), e.toString());
				results[exec] = e.toString();
				e.printStackTrace();
			}
			catch (Error err) {
				results[exec] = err.toString();
				System.out.println(err);
				return;
			}

			
			//writeResult(message);
			dao.updateEntityExec(entityId, 0, results[0], results[1], results[2]);
		}
	}
	
	
	private void writeResult(String text) {
		try {
			File resultFile = new File(resultFilename);
			BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile, true));
			writer.write(text + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private Class<?> getClassByName(String className) {

		
		if (className.toLowerCase().contains("boolean")) {
			return boolean.class;
		} else if (className.toLowerCase().contains("char")) {
			return char.class;
		} else if (className.toLowerCase().contains("byte")) {
			return byte.class;
		} else if (className.toLowerCase().contains("short")) {
			return short.class;
		} else if (className.toLowerCase().contains("int")) {
			return int.class;
		} else if (className.toLowerCase().contains("long")) {
			return long.class;
		} else if (className.toLowerCase().contains("float")) {
			return float.class;
		} else if (className.toLowerCase().contains("double")) {
			return double.class;
		} else if (className.toLowerCase().contains("string")) {
			return String.class;
		}
		
		return null;
	}

	
	private Object getValueByClass(String className, int numExec) {

		if (className.toLowerCase().contains("boolean")) {
			return booleanValues[numExec];
		} else if (className.toLowerCase().contains("char")) {
			return charValues[numExec];
		} else if (className.toLowerCase().contains("byte")) {
			return byteValues[numExec];
		} else if (className.toLowerCase().contains("short")) {
			return shortValues[numExec];
		} else if (className.toLowerCase().contains("int")) {
			return intValues[numExec];
		} else if (className.toLowerCase().contains("long")) {
			return longValues[numExec];
		} else if (className.toLowerCase().contains("float")) {
			return floatValues[numExec];
		} else if (className.toLowerCase().contains("double")) {
			return doubleValues[numExec];
		} else if (className.toLowerCase().contains("string")) {
			return stringValues[numExec];
		}
		return null;
	}

	/**
	 * This method compare the result of two jars. 
	 * 
	 */
	public void compareJars() {
		
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
		String jarMethods = prop.getProperty("jar-methods");
		
		File folder = new File(jarMethods);
		File[] files = folder.listFiles();

		//Get all pairs 
		InterfaceMetricsDao dao = new InterfaceMetricsDao();
		List<int[]> idPairs = new ArrayList<>();		
		for (File f : files) {
			
			int entityId = Integer.parseInt(f.getName().substring(0, f.getName().length() - 4));
			List<int[]> tmp = dao.getSimilar(entityId);
			if (tmp != null && tmp.size() > 0) 
				idPairs.addAll(tmp);
		}
		
		for (int[] pair : idPairs) {
			String[] resultsMethod1 = dao.getResults(pair[0]);
			String[] resultsMethod2 = dao.getResults(pair[1]);
			
			if (resultsMethod1 == null || resultsMethod2 == null) {
				System.out.println(MessageFormat.format("Get Result error: {0} and {1}\n\n", pair[0], pair[1]));
				continue;
			}
			
			boolean[] execs = new boolean[4];
			
			execs[1] = compareExec(resultsMethod1[1], resultsMethod2[1]); 
			execs[2] = compareExec(resultsMethod1[2], resultsMethod2[2]);
			execs[3] = compareExec(resultsMethod1[3], resultsMethod2[3]);
			
			int count = 0;
			for (int i = 1; i < execs.length; i++) {
				if (execs[i])
					count++;
			}
			
			String message = MessageFormat.format("{0, number, #};{1, number, #};{2};{3};{4};{5, number, #.##%}", 
												  pair[0], pair[1], execs[1], execs[2], execs[3], count/3.0);
			
			System.out.println(message);
			
		}

	}
	
	/**
	 * Compare executions results and indicates if are similar or not
	 * 
	 * @param result1 Result of first method
	 * @param result2 Result of second method
	 * 
	 * @return true - Similar
	 *         false - Not similar
	 */
	private boolean compareExec(String result1, String result2) {
		
		if ((result1 == null) && (result2 == null))
			return true;
		
		if ((result1 != null) && result1.equals(result2))
			return true;
		
		return false;
	}
	
	
	
}


