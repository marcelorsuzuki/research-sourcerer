package br.unifesp.ict.seg.smis.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import br.unifesp.ict.seg.smis.application.SliceService;
import br.unifesp.ict.seg.smis.dao.InterfaceMetricsDao;
import br.unifesp.ict.seg.smis.dao.ProjectDao;
import br.unifesp.ict.seg.util.ManipulateFile;

public class MethodsProcess {

	private String filename;

	public MethodsProcess() {}
	
	public MethodsProcess(String filenameIds) {
		this.filename = filenameIds;
	}

	
	public void compileAll() throws IOException {
		
		final boolean step1 = false;
		final boolean step2 = false;
		final boolean step3 = false;
		final boolean step4 = true;
		final boolean step5 = true;
		
		Properties prop = new Properties();
		InputStream input = new FileInputStream("smis.properties");
		
		prop.load(input);
		String execDir = prop.getProperty("exec-folder");
		input.close();
		
		String sliceDir = execDir + "slice/";
		String tempDir = execDir + "temp/";
		String jarDir = execDir + "jar-methods/";
		
		//Faz a leitura do arquivo para obter os ids de "entity_metrics"
		Path path = Paths.get(filename);
		//List<String> lines = Files.readLines(file, Charsets.UTF_8);
		List<String> lines = Files.readAllLines(path);
		
		Map<Integer, Boolean> entityIdMap = new HashMap<>();
		
		if (step1) {
			System.out.println("\n---------------- Step 1 - Get all Entity ID's ---------------------------\n");
			entityIdMap = returnAllEntiyIds(lines);
		}
		
		//		
//		entityIdMap = new HashMap<>();
//		entityIdMap.put(16946, true);
//		entityIdMap.put(16947, true);
//		entityIdMap.put(17053, true);
//		entityIdMap.put(17054, true);
		
		entityIdMap.put(13145549, false);	//TIMEOUT
		entityIdMap.put(12859016, false);
		entityIdMap.put(12701811, false);	//ERROR
		entityIdMap.put(12750975, false);
		entityIdMap.put(13173024, false);	//TIMEOUT
		entityIdMap.put(12718836, false);
		entityIdMap.put(13018316, false);	//TIMEOUT
		entityIdMap.put(13166013, false);	//ERROR
		entityIdMap.put(13229351, false);	//ERROR
		entityIdMap.put(13229369, false);	//TIMEOUT
		entityIdMap.put(13229360, false);	//TIMEOUT
		entityIdMap.put(13022966, false);	//TIMEOUT
		entityIdMap.put(13164166, false);	//TIMEOUT
		entityIdMap.put(12752440, false);	//TIMEOUT
		entityIdMap.put(12742496, false);	//TIMEOUT
		
//		entityIdMap.put(12783764, false);	//ERROR
//		entityIdMap.put(12861926, false);	//ERROR
//		entityIdMap.put(12772847, false);	//ERROR
//		entityIdMap.put(12853225, false);	//ERROR
//		entityIdMap.put(12740850, false);	//ERROR
//		entityIdMap.put(12783754, false);	//ERROR
//		entityIdMap.put(12783753, false);	//ERROR
//		entityIdMap.put(12783758, false);	//ERROR
//		entityIdMap.put(12783757, false);	//ERROR
//		entityIdMap.put(12783784, false);	//ERROR
//		entityIdMap.put(12783789, false);	//ERROR
//		entityIdMap.put(12783788, false);	//ERROR
//		entityIdMap.put(12783789, false);	//ERROR
		entityIdMap.put(12757178, false);
//		entityIdMap.put(12787899, false);	//ERROR
		
		
		
//		entityIdMap.put(12740851, false);	
//		entityIdMap.put(12783752, false);	
//		entityIdMap.put(12783755, false);	
//		entityIdMap.put(12783756, false);	
//		entityIdMap.put(12783757, false);
//		entityIdMap.put(12783759, false);
//		entityIdMap.put(12783760, false);	
//		entityIdMap.put(12783761, false);	
//		entityIdMap.put(12783762, false);	
//		entityIdMap.put(12783763, false);	
//		entityIdMap.put(12783764, false);
//		entityIdMap.put(12783765, false);
//		entityIdMap.put(12783766, false);	
//		entityIdMap.put(12783767, false);	
//		entityIdMap.put(12783768, false);	
//		entityIdMap.put(12783769, false);	
		
		if (step2) {
			System.out.println("\n------------------------ Step 2 - Slice ---------------------------------\n");
			int numSlice = sliceRetriveZips(sliceDir, entityIdMap);
			System.out.println("\nNumber of methods sliced: " + numSlice + "\n");
			
		}		
		
		
		if (step3) {
			System.out.println("\n------------------ Step 3 - Extract zip file ----------------------------\n");
			int numExtracted = extractMethodSliced(sliceDir, tempDir);
			System.out.println("\nNumber of zip files extracted: " + numExtracted + "\n");
		}
		
		
		if (step4) {
			System.out.println("\n----------------- Step 4 - Generate \"build.xml\" -------------------------\n");
			int numFiles = generateBuildXml(tempDir, jarDir);
			System.out.println("\nNumber of \"build.xml\" generated: " + numFiles + "\n");
		}

		
		if (step5) {
			System.out.println("\n----------------- Step 5 - Generate \"build.xml\" -------------------------\n");
			int numJars = generateJar(tempDir);
			System.out.println("\nNumber of jars generated: " + numJars + "\n");
		}
		
		
//		Alert alert = new Alert(AlertType.INFORMATION);
//		alert.setTitle("Information Dialog");
//		alert.setHeaderText(null);
//		alert.setContentText("All methods were extracted and compiled!");
//
//		alert.showAndWait();

		
	}
	
	
	/**
	 * 
	 * Get all "entity_id" from "entity_metrics"
	 * 
	 * @param intefaceMetricsId Id list from "entity_metrics"
	 * 
	 * @return Integer list with all EntityIds 
	 */
	private Map<Integer, Boolean> returnAllEntiyIds(List<String> intefaceMetricsId) {
		
		Map<Integer, Boolean> ids = new HashMap<>();
		
		
		InterfaceMetricsDao dao = new InterfaceMetricsDao();

		for (int i = 0; i < intefaceMetricsId.size(); i++) {
			
			String s = intefaceMetricsId.get(i);
			
			if (s.startsWith("#"))
				continue;
			
			Integer entityId = dao.retriveEntityId(Integer.parseInt(s));
			
			if (entityId != null) {
				ids.put(entityId, true);
				System.out.println("DAO - (" + (i + 1) + "/" + intefaceMetricsId.size() + ") Id: " + s + " and Entity Id: " + entityId);
			}

		}

		System.out.println("DAO - Total: " + ids.size() + " metodos 'CRAWLED'");
		return ids;
	}

	
	
	/**
	 * This method executes slice and return the name of zip files.
	 * 
	 * @param sliceDir Folder to download the zip files from slice
	 * @param entityIdList List of id's 
	 * 
	 * @return Map with zip filename to each entity id
	 * 
	 * @throws IOException
	 */
	private int sliceRetriveZips(String sliceDir, Map<Integer, Boolean> entityIdList) {
		
		int count = 1;
		int numSlice = 0;
		for (Integer entityId : entityIdList.keySet()) {

			System.out.println("\n-------------------------------------------------------");
			System.out.println("SLICE - " + count + " of " + entityIdList.size() + ". Entity Id: " + entityId + "(" + entityIdList.get(entityId) + ")");

			
			//Get the id of entity and execute slice
			if (entityIdList.get(entityId)) {
				String zipFile = sliceDir + entityId.toString() + ".zip";
				
				File f = new File(zipFile);
				if (!f.exists()) {
					SliceService service = new SliceService(zipFile);
					
					if (service.findMethod(entityId)) {
						numSlice++;
					}
				}
			}
			
			count++;
		}
		
		return numSlice;
	}

	
	/**
	 * Extract all zip files with methods sliced and create a map with folder name to each entity id.
	 * 
	 * @param inputDir Folder with zip files
	 * @param outputDir Folder to extract the zip files
	 * 
	 * @return Number of zip files extracted.
	 */
	private int extractMethodSliced(String inputDir, String outputDir) {

		File folder = new File(inputDir);
		File[] files = folder.listFiles();
		
		int numExtracted = 0;
		for (File f : files) {
			String entityId = f.getName().substring(0, f.getName().length() - 4);
			String zipFile = f.getAbsolutePath();
			if (zipFile != null) {

				String srcDir = outputDir + entityId + "/src/";
				ManipulateFile.extract(zipFile, srcDir);
				numExtracted++;
			}
			
			System.out.println("\n-------------------------------------------------------");
			System.out.println("EXTRACT - " + zipFile + ". Entity Id: " + entityId);
		}
		return numExtracted;
		
	}

	

	/**
	 * This method generates the "build.xml" to ant program and return the full pathname to build.xml
	 * 
	 * @param inputDir Folder with source code
	 * @param outputDir Folder to create jar file
	 * 
	 * @return Number of "build.xml" files were generated 
	 */
	private int generateBuildXml(String inputDir, String outputDir) {

		File folder = new File(inputDir);
		File[] dirs = folder.listFiles();
		
		String repo = "";
		
		try {
			Properties prop = new Properties();
			InputStream input = new FileInputStream("smis.properties");
			prop.load(input);
			repo = prop.getProperty("input-repo");
			input.close();		
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int numFiles = 0;
		for (File f : dirs) {
			if (f.isDirectory()) {
				String entityId = f.getName();
				String prjDir = f.getAbsolutePath() + "/";

				String pathProj = null;
				if (!repo.isEmpty()) {
					ProjectDao dao = new ProjectDao();
					pathProj = dao.getPathByEntityId(Integer.parseInt(entityId));
					if (pathProj != null) {
						pathProj = repo + pathProj + "/content/lib";
					}
				}
				
				//Configura o "build.xml" do ant
				String srcDir = prjDir + "src/";
				String buildDir = prjDir + "build/";
				String jarFile = outputDir + entityId + ".jar";
				String buildFile = prjDir + "build.xml";

				String tagPath = "";
				String tagClasspath = "";
				if (!pathProj.isEmpty()) {
					
					tagPath = "    <path id=\"class.path\">\n"
							+ "        <fileset dir=\"" + pathProj + "\">\n"
							+ "            <include name=\"**/*.jar\" />\n"
							+ "        </fileset>"
							+ "    </path>";
					
					tagClasspath = "            <classpath refid=\"class.path\" />\n";
				}
				
				
				Path path = Paths.get(buildFile);
				BufferedWriter xml;
				try {
					
					//TODO Path of javac can't be hard coding
					
					xml = Files.newBufferedWriter(path);
					xml.write("<project>\n");
					xml.write(	   tagPath);
					xml.write("    <target name=\"compile\">\n");
					xml.write("        <mkdir dir=\"" + buildDir + "\" />\n");
					xml.write("        <javac srcdir=\"" + srcDir + "\"\n"); 
					xml.write("               destdir=\"" + buildDir + "\"\n" );
					xml.write("               executable=\"C:/Program Files/Java/jdk1.8.0_131/bin/javac.exe\" fork=\"true\"  taskname=\"javac1.8\">\n");
					xml.write(             tagClasspath);
					xml.write("        </javac>\n");					
					xml.write("        <jar destfile=\"" + jarFile + "\"\n"); 
					xml.write("             basedir=\"" + buildDir + "\" />\n");
					xml.write("    </target>\n");
					xml.write("</project>");
					xml.close();
					
					numFiles++;
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("\n-------------------------------------------------------");
				System.out.println("BUILD.XML - " + buildFile + ". Entity Id: " + entityId);

			}
		}
		
		return numFiles;
	}
	
	
	 /**
     * Execute a target specified in the Ant build.xml file
     * 
     * @param inputDir Folder with source code to compile using "ant"
     * 
     * @return true: Ok 		<br/>
     *         false: Error
     * 
     */
    private int generateJar(String inputDir) {
    	
		File folder = new File(inputDir);
		File[] dirs = folder.listFiles();
		
		int numFiles = 0;
		for (File f : dirs) {
			if (f.isDirectory()) {
				String entityId = f.getName();
				String buildXml = f.getAbsolutePath() + "/build.xml";

				
	            // Prepare Ant project
	            Project project = new Project();
	            File buildFile = new File(buildXml);
	            project.setUserProperty("ant.file", buildFile.getAbsolutePath());
	     
	            // Capture event for Ant script build start / stop / failure
	            try {
	                project.fireBuildStarted();
	                project.init();
	                ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
	                project.addReference("ant.projectHelper", projectHelper);
	                projectHelper.parse(project, buildFile);
	                 
	                // If no target specified then default target will be executed.
	                project.executeTarget("compile");
	                project.fireBuildFinished(null);
	                numFiles++;
	            } catch (BuildException buildException) {
	                project.fireBuildFinished(buildException);
	                System.out.println("ERROR - " + buildXml + ". Entity Id: " + entityId);
	            }
	            
				System.out.println("\n-------------------------------------------------------");
				System.out.println("JAR - " + buildXml + ". Entity Id: " + entityId);
            }
            
    	}
    	
    	
    	return numFiles;
    }
	
}
