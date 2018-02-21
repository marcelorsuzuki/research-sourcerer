package br.unifesp.ict.seg.smis.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import br.unifesp.ict.seg.smis.dao.InterfaceMetricsDao;

public class MethodsResult {

	private String filename;

	public MethodsResult(String filename) {
		this.filename = filename;
	}
	
	public void showResult() {
		List<Integer> ids = getListIdCrawled();
		List<String> zips = getListZipFiles(ids);
		List<String> jars = getListJarFiles(ids);
		List<String> jarsExecuted = getListJarExecuted(ids);
		List<String> jarsExecutedNoError = getListJarExecutedWithoutError(ids);
		
		System.out.println("\nExecution result:");
		System.out.println("Filename: " + this.filename);
		System.out.println("Number of ids: " + ids.size());
		System.out.println(MessageFormat.format("Number of zips: {0} - {1, number, #.##%}", zips.size(), (float) zips.size() / ids.size()));
		System.out.println(MessageFormat.format("Number of jars: {0} - {1, number, #.##%}", jars.size(), (float) jars.size() / ids.size()));
		System.out.println(MessageFormat.format("Number of jars executed: {0} - {1, number, #.##%}", jarsExecuted.size(), (float) jarsExecuted.size() / ids.size()));
		System.out.println(MessageFormat.format("Number of jars executed without error: {0} - {1, number, #.##%}", jarsExecutedNoError.size(), (float) jarsExecutedNoError.size() / ids.size()));
		
		//MessageFormat.format("{0, number, #};{1, number, #};{2};{3};{4};{5};{6, number, #.##%};{7}", 
	}
	
	
	/**
	 * 
	 * Get all "entity_id" from "entity_metrics"
	 * 
	 * @param intefaceMetricsId Id list from "entity_metrics"
	 * 
	 * @return Integer list with all EntityIds 
	 */
	public List<Integer> getListIdCrawled() {

		//Faz a leitura do arquivo para obter os ids de "entity_metrics"
		Path path = Paths.get(filename);

		List<String> lines;
		try {
			lines = Files.readAllLines(path);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		List<Integer> ids = new ArrayList<>();
		
		InterfaceMetricsDao dao = new InterfaceMetricsDao();

		for (int i = 0; i < lines.size(); i++) {
			
			String s = lines.get(i);
			
			if (s.startsWith("#"))
				continue;
			
			Integer entityId = dao.retriveEntityId(Integer.parseInt(s));
			
			if (entityId != null) {
				ids.add(entityId);
			}

		}
		return ids;
	}

	
	public List<String> getListZipFiles(List<Integer> ids) {

		try {
			
			List<String> zips = new ArrayList<>();
			
			Properties prop = new Properties();
			InputStream input = new FileInputStream("smis.properties");
			
			prop.load(input);

			String execDir = prop.getProperty("exec-folder");
			input.close();
			
			String sliceDir = execDir + "slice/";

			for (Integer entityId : ids) {
				
				String zipFile = sliceDir + entityId.toString() + ".zip";
				
				File f = new File(zipFile);
				if (f.exists()) {
					zips.add(zipFile);
				}
			}
			return zips;
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	
	public List<String> getListJarFiles(List<Integer> ids) {

		try {
			
			List<String> jars = new ArrayList<>();
			
			Properties prop = new Properties();
			InputStream input = new FileInputStream("smis.properties");
			
			prop.load(input);

			String execDir = prop.getProperty("exec-folder");
			input.close();
			
			String jarDir = execDir + "jar-methods/";

			for (Integer entityId : ids) {
				
				String jarFile = jarDir + entityId.toString() + ".jar";
				
				File f = new File(jarFile);
				if (f.exists()) {
					jars.add(jarFile);
				}
			}
			return jars;
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	
	public List<String> getListJarExecuted(List<Integer> ids) {

		try {
			
			InterfaceMetricsDao dao = new InterfaceMetricsDao();
			
			List<String> jars = new ArrayList<>();
			
			Properties prop = new Properties();
			InputStream input = new FileInputStream("smis.properties");
			
			prop.load(input);

			String execDir = prop.getProperty("exec-folder");
			input.close();
			
			String jarDir = execDir + "jar-methods/";

			for (Integer entityId : ids) {

				String jarFile = jarDir + entityId.toString() + ".jar";				
				File f = new File(jarFile);
				if (f.exists()) {
					if (dao.getErrorExecutedMethod(entityId) != null) {
						jars.add(jarFile);
					}
				
				}				
			}
			return jars;
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	
	public List<String> getListJarExecutedWithoutError(List<Integer> ids) {

		try {
			
			InterfaceMetricsDao dao = new InterfaceMetricsDao();
			
			List<String> jars = new ArrayList<>();
			
			Properties prop = new Properties();
			InputStream input = new FileInputStream("smis.properties");
			
			prop.load(input);

			String execDir = prop.getProperty("exec-folder");
			input.close();
			
			String jarDir = execDir + "jar-methods/";

			for (Integer entityId : ids) {
				
				String jarFile = jarDir + entityId.toString() + ".jar";				
				File f = new File(jarFile);
				if (f.exists()) {
					Integer error = dao.getErrorExecutedMethod(entityId);
					if ((error != null) && (error.intValue() == 0)) {
						jars.add(jarFile);
					}
				}				
				
			}
			return jars;
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
}
