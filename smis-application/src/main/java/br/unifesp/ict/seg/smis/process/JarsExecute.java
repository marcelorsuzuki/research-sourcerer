package br.unifesp.ict.seg.smis.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class JarsExecute {
	
	//3125654, 3139380, 3182716, 3182721
	private final int lastIMId = 3182721;
	
	private final int NUMBER_EXECS = 4;
	

	private final boolean[]  booleanValues = {true, true, false, true};
	private final char[] charValues = {'m', 'K', ' ', '-'};
	private final byte[] byteValues = {'9', '\n', '\0', 9};
	private final short[] shortValues = {25, 0, -13, 7};
	private final int[] intValues = {25, 0, -13, 7};
	private final long[] longValues = {25, 0, -13, 7};
	private final float[] floatValues = {25.2f, 0.0f, -13.8f, 7.13f};
	private final double[] doubleValues = {25.2, 0.0, -13.8, 7.13};
	private final String[] stringValues = {"C:/_mestrado/smis-test/tempfile.txt",
			"This string can be replaced, changing the value \"235\" by the filename.\nIf you have any question, send an email to mrsuzuki@gmail.com", 
			"235", "long"};
	
	private String execFolder;
	//private int idxParam = 0;
	
	private List<Integer> interfaceMetricsIds = new ArrayList<>();
	private Map<String, File> jarsLib = new HashMap<>();
	private Map<Integer, File> jarsCompiled = new HashMap<>();
	
	public JarsExecute(String filenameIds) {
		
		try {
			//Faz a leitura do arquivo para obter os ids de "entity_metrics"
			Path path = Paths.get(filenameIds);
			List<String> lines = Files.readAllLines(path);
			
			for (String s : lines) {
				if (s.startsWith("#")) {
					continue;
				}
					
				interfaceMetricsIds.add(Integer.parseInt(s));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
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
		
		execFolder = prop.getProperty("exec-folder");
		String inputRepo = prop.getProperty("input-repo");
		
		//Map Jars compiled and Jars libraries
		jarsCompiled = getJarsCompiled(execFolder + "jar-methods");
		jarsLib = getJarsLib(inputRepo + "1");
		
		int count = 0;
		for (Integer id : interfaceMetricsIds) {
			if (id.intValue() > lastIMId) {
				count++;
				System.out.println(MessageFormat.format("{0, number, #} - {1, number, #}", count, id));
				executeJar(id);
			}
		}
	}
	
	
	public Map<Integer, File> getJarsCompiled(String path) {
		
		Map<Integer, File> jars = new HashMap<>();
		
		File folder = new File(path);
		File[] files = folder.listFiles();
		
		for (File f : files) {
			if (f.getName().endsWith(".jar")) {
				String tmp = f.getName().substring(0, f.getName().length() - 4);
				Integer entityId = Integer.parseInt(tmp);
				jars.put(entityId, f);
			}
		}
		
		return jars;
	}
	
	
	public Map<String, File> getJarsLib(String path) {

		Map<String, File> jars = new HashMap<>();
		
		File folder = new File(path);
		File[] files = folder.listFiles();
		
		for (File f : files) {
			
			if (f.isDirectory()) {
				File libDir = new File(f.getAbsolutePath(), "content/lib");
				File[] libs = libDir.listFiles();
				for (File lib : libs) {
					if (lib.getName().endsWith(".jar")) {
						jars.put(lib.getName(), lib);
					}
				}
			}
		}
		
		return jars;
		
	}

	/**
	 * This method execute a method into a specific jar
	 *  
	 * @param interfaceMetricId
	 * @throws InterruptedException 
	 */
	public void executeJar(Integer interfaceMetricId)  {

		InterfaceMetricsDao dao = new InterfaceMetricsDao();
		EntityInfo entityInfo = dao.getEntityInfo(interfaceMetricId);
		
		Class<?> myClass = null;
		Method method = null;

		try {

			if (entityInfo.getProjectType().equals("CRAWLED")) {
				if (!jarsCompiled.containsKey(entityInfo.getEntityId())) {
					System.out.println(MessageFormat.format("Compiled jar {0, number, #} not found.\n", entityInfo.getEntityId()));
					return;
				}
					
				URL[] myJars = new URL[1];
				myJars[0] = jarsCompiled.get(entityInfo.getEntityId()).toURI().toURL();
				URLClassLoader child = new URLClassLoader(myJars, this.getClass().getClassLoader());
				myClass = Class.forName(entityInfo.getClassName(), true, child);
			}
			
			if (entityInfo.getProjectType().equals("JAR")) {
				
				if (!jarsLib.containsKey(entityInfo.getProjectName())) {
					System.out.println(MessageFormat.format("Library {0} not found.\n", entityInfo.getProjectName()));
					return;
				}
				
				File jar = jarsLib.get(entityInfo.getProjectName());
				File path = new File(jar.getParent());
				File[] files = path.listFiles();
				
				URL[] myJars = new URL[files.length];
				for (int i = 0; i < files.length; i++) {
					myJars[i] = files[i].toURI().toURL();
				}
				URLClassLoader child = new URLClassLoader(myJars, this.getClass().getClassLoader());
				myClass = Class.forName(entityInfo.getClassName(), true, child);
			}
			
			if (entityInfo.getProjectType().equals("JAVA_LIBRARY")) {
				myClass = Class.forName(entityInfo.getClassName());
			}
			
			//Get the type of params used on method
			Class<?>[] params = new Class<?>[entityInfo.getParamList().size()];
			for (int i = 0; i < entityInfo.getParamList().size(); i++) {
				params[i] = getClassByName(entityInfo.getParamList().get(i));
			}
			
			//Get the method to be executed
			method = myClass.getMethod(entityInfo.getMethodName(), params);
		}
		catch (MissingResourceException | MalformedURLException | NoSuchMethodException | SecurityException | ClassNotFoundException e){
			String[] results = {e.toString(), null, null, null};
			String[] execs = {null, null, null, null};
			dao.updateEntityExec(entityInfo.getEntityId(), -1, results, execs);
			System.out.println(MessageFormat.format("ERROR - Id: {0, number, #} - Project Type: {1}\n", interfaceMetricId, entityInfo.getProjectType()));
			return;
		}
		catch (Error err) {
			String[] results = {err.toString(), null, null, null};
			String[] execs = {null, null, null, null};
			dao.updateEntityExec(entityInfo.getEntityId(), -1, results, execs);
			System.out.println(err);
			System.out.println(MessageFormat.format("ERROR - Id: {0, number, #} - Project Type: {1}\n", interfaceMetricId, entityInfo.getProjectType()));
			return;
		}
		

		String [] results = new String[NUMBER_EXECS];
		String [] syntaxeCall = new String[NUMBER_EXECS];
		int error = 0;
		
		//Execute the method NUMBER_EXECS, with differents inputs 
		for (int exec = 0; exec < NUMBER_EXECS; exec++) {
			
			generateFile();
			try {
				//Get the values defined to be used as input. The value used depends of param type and execution time
				Object[] values = new Object[entityInfo.getParamList().size()];
				for (int i = 0; i < entityInfo.getParamList().size(); i++) {
					int idxParam = generateIndex(exec, i);
					values[i] = getValueByClass(entityInfo.getParamList().get(i), idxParam);
				}

				syntaxeCall[exec] = buildSyntaxeText(myClass, values, method);
				
				//The method is executed by a thread to have a timeout implemented
				//ThreadExecute threadExec = new ThreadExecute(myClass, values, method);
				ThreadExecObject threadExec = new ThreadExecObject(myClass, values, method);
				Thread thread = new Thread(threadExec);
				thread.start();
				thread.join(300000);

				if (thread.isAlive()) {
					error = 2;
					results[exec] = "Timeout";
					thread.interrupt();
				}
				else {
					error = 0;
					results[exec] = MessageFormat.format("{0}", threadExec.getObj());
				}
			} catch (Exception e) {
				if (e instanceof InvocationTargetException)
					results[exec] = "Exception: " + e.getCause().toString();
				else
					results[exec] = "Exception: " + e.toString();
				
				error = 3;
				e.printStackTrace();
			}
			catch (Error err) {
				error = 4;
				results[exec] = "Error: " + err.toString();
				System.out.println(err);
				continue;
			}
			
		}
		dao.updateEntityExec(entityInfo.getEntityId(), error, results, syntaxeCall);
		System.out.println(MessageFormat.format("Id: {0, number, #} - Project Type: {1}\n", interfaceMetricId, entityInfo.getProjectType()));
	}
	
	private void generateFile() {
		
		Path source = Paths.get(execFolder + "tempfile-original.txt"); 
		Path dest = Paths.get(stringValues[0]);
		try {
			Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private int generateIndex(int numExec, int idxParam) {
		if (numExec == 0) {
			return 0;
		}
		
		return (numExec + idxParam) % NUMBER_EXECS;
	}
	
	private String buildSyntaxeText(Class<?> myClass, Object[] values, Method method) {
		String param = "(";
		
		for (int i = 0; i < values.length; i++) {
			
			String tmp = values[i].toString();;
			if (values[i].getClass().equals(String.class)) {
				tmp = "\"" + tmp + "\"";
			}
					
			if (i <  values.length - 1) {
				param += tmp + ",";
			}
			else {
				param += tmp;
			}
		}
		param += ")";

		return myClass.getName() + "." + method.getName() + param;

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

	
	private Object getValueByClass(String className, int idx) {

		if (className.toLowerCase().contains("boolean")) {
			return booleanValues[idx];
		} else if (className.toLowerCase().contains("char")) {
			return charValues[idx];
		} else if (className.toLowerCase().contains("byte")) {
			return byteValues[idx];
		} else if (className.toLowerCase().contains("short")) {
			return shortValues[idx];
		} else if (className.toLowerCase().contains("int")) {
			return intValues[idx];
		} else if (className.toLowerCase().contains("long")) {
			return longValues[idx];
		} else if (className.toLowerCase().contains("float")) {
			return floatValues[idx];
		} else if (className.toLowerCase().contains("double")) {
			return doubleValues[idx];
		} else if (className.toLowerCase().contains("string")) {
			return stringValues[idx];
		}
		return null;
	}

}


