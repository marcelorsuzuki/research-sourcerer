
package br.unifesp.ict.seg.smis.application;
	
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import br.unifesp.ict.seg.smis.process.JarsCompare;
import br.unifesp.ict.seg.smis.process.JarsExecute;
import br.unifesp.ict.seg.smis.process.MethodsProcess;
import br.unifesp.ict.seg.smis.process.MethodsResult;
import edu.uci.ics.sourcerer.tools.java.repo.model.JavaRepositoryFactory;
import edu.uci.ics.sourcerer.util.io.arguments.ArgumentManager;
import edu.uci.ics.sourcerer.utils.db.DatabaseConnectionFactory;


/**
 * Classe principal do programa
 * 
 * @author Marcelo Suzuki
 *
 */
//public class SmisApplication extends Application {
public class SmisApplication {
	
	private static final boolean PROCESS_ALL_METHODS = false;
	private static final boolean EXECUTE_ALL_JARS = false;
	private static final boolean COMPARE_ALL_JARS = false;
	private static final boolean SHOW_RESULTS = true;


//	/**
//	 * Método que carrega a janela principal, chamado pelo JavaFX
//	 * @see javafx.application.Application#start(javafx.stage.Stage)
//	 */
//	@Override
//	public void start(Stage primaryStage) {
//
//		//Tamanho da janela maximixada
////		double width = Screen.getPrimary().getVisualBounds().getWidth(); 
////		double height = Screen.getPrimary().getVisualBounds().getHeight(); 
//		double width = 700;
//		double height = 300;
//		
//		try {
//			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("../view/main.fxml"));
//			Scene scene = new Scene(root, width, height);
//			primaryStage.setScene(scene);
//			//primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Info_i_blue.png")));
//			primaryStage.setTitle(" SMIS 1.0");
//			primaryStage.show();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	
	/**
	 * Entry point do programa
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		InputStream is;
		try {
			
			is = new FileInputStream("smis.properties");
			ArgumentManager.PROPERTIES_STREAM.setValue(is);
			JavaRepositoryFactory.INPUT_REPO.permit();
			DatabaseConnectionFactory.DATABASE_URL.permit();
			DatabaseConnectionFactory.DATABASE_USER.permit();
			DatabaseConnectionFactory.DATABASE_PASSWORD.permit();
			ArgumentManager.initializeProperties();		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		//launch(args);
		
		
		if (PROCESS_ALL_METHODS) {
			try {
				MethodsProcess methodProcess = new MethodsProcess("ids_v3.txt");
				methodProcess.compileAll();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (EXECUTE_ALL_JARS) {
			JarsExecute jarsExecute = new JarsExecute("ids_v3.txt");
			jarsExecute.executeAllJars();
		}
		
		if (COMPARE_ALL_JARS) {
			JarsCompare jarsCompare = new JarsCompare();
			jarsCompare.compareAll();
		}
		
		if (SHOW_RESULTS) {
			
			MethodsResult result = new MethodsResult("ids_v2.txt");
			result.showResult();
			
			result = new MethodsResult("ids_v3.txt");
			result.showResult();
		}
	}
}

