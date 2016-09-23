package br.unifesp.ict.seg.smis.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import br.unifesp.ict.seg.smis.SliceService;
import br.unifesp.ict.seg.smis.dao.EntityDao;
import br.unifesp.ict.seg.util.ManipulateFile;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MethodProcess {

	private String filename;

	public MethodProcess() {}
	
	public MethodProcess(String filename) {
		this.filename = filename;
	}

	
	public void compileAll() throws IOException {
		
		//Obtém a pasta do arquivo
		filename = filename.replace("\\", "/");
		int pos = filename.lastIndexOf("/");
		String currentDir = filename.substring(0, pos + 1);
		
		//TODO Diretórios não podem ser hard coding
		String sliceDir = currentDir + "slice/";
		String tempDir = currentDir + "temp/";
		
		
		//Faz a leitura do arquivo para obter os ids de "entity_metrics"
		File file = new File(filename);
		List<String> lines = Files.readLines(file, Charsets.UTF_8);

		
		//Cria arquivo de log
		File log = new File(currentDir + "output.log");
		BufferedWriter output = Files.newWriter(log, Charsets.UTF_8);
		
		//Loop para cada id de "entity_metrics"
		for (int i = 0; i < lines.size(); i++) {
			
			String s = lines.get(i);
			
			//Obtém o id de entity
			EntityDao dao = new EntityDao();
			Integer entityId = dao.retriveEntityId(s);
			
			System.out.println("\n\n-------------------------------------------------------");
			System.out.println((i + 1) + " of " + lines.size() + ". Id: " + s + " and Entity Id: " + entityId + "\n\n");
			
			//Faz o slice do entity
			System.out.println("\nStep 1 - Slice");
			String zipFile = sliceDir + entityId.toString() + ".zip";
			SliceService service = new SliceService(zipFile);
			if (!service.findMethod(entityId)) {
				output.write(entityId + " - SLICE ERROR");
				continue;
			}
			
		
			//Extrai o zip obtido do slicer
			String srcDir = tempDir + entityId + "/src/";
			System.out.println("\nStep 2 - Unzip");
			ManipulateFile.extract(zipFile, srcDir);
		
			//Configura o "build.xml" do ant
			System.out.println("\nStep 3 - Generate Xml");
			String buildDir = tempDir + entityId + "/build/";
			String jarFile = currentDir + "jar-methods/" + entityId + ".jar";
			String xmlFile = generateBuildXml(tempDir + entityId + "/", srcDir, buildDir, jarFile);
			if (xmlFile == null) {
				output.write(entityId + " - GENERATE BUILD.XML ERROR");
				continue;
			}
			
		
			//Cria o jar do entity
			System.out.println("\nStep 4 - Generate Jar");
			if (!generateJar(xmlFile)) {
				output.write(entityId + " - GENERATE JAR FILE ERROR");
			}
			
			
		}
			
		output.close();
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText("All methods were extracted and compiled!");

		alert.showAndWait();

		
	}
	
	private String generateBuildXml(String tempDir, String srcDir, String buildDir, String jarFile) {
		
		String buildFile = tempDir + "build.xml";
		File file = new File(buildFile);
		BufferedWriter xml;
		try {
			xml = Files.newWriter(file, Charsets.UTF_8);
			xml.write("<project>\n");
			xml.write("    <target name=\"compile\">\n");
			xml.write("        <mkdir dir=\"" + buildDir + "\" />\n");
			xml.write("        <javac srcdir=\"" + srcDir + "\"\n"); 
			xml.write("               destdir=\"" + buildDir + "\"" );
			xml.write("               executable=\"/usr/bin/javac\" fork=\"true\"  taskname=\"javac1.8\" />\n");
			xml.write("        <jar destfile=\"" + jarFile + "\"\n"); 
			xml.write("             basedir=\"" + buildDir + "\" />\n");
			xml.write("    </target>");
			xml.write("</project>");
			xml.close();
			
			return buildFile;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	
	 /**
     * To execute a target specified in the Ant build.xml file
     * 
     * @param buildXmlFileFullPath
     * @param target
     */
    private boolean generateJar(String buildXmlFileFullPath) {
        boolean success = false;
 
        // Prepare Ant project
        Project project = new Project();
        File buildFile = new File(buildXmlFileFullPath);
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
            success = true;
        } catch (BuildException buildException) {
            project.fireBuildFinished(buildException);
            throw new RuntimeException("!!! Unable to restart the IEHS App !!!", buildException);
        }
         
        return success;
    }
	
}
