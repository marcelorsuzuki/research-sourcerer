
package br.unifesp.ict.seg.smis;
	
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import edu.uci.ics.sourcerer.tools.java.repo.model.JavaRepositoryFactory;
import edu.uci.ics.sourcerer.util.io.arguments.ArgumentManager;
import edu.uci.ics.sourcerer.utils.db.DatabaseConnectionFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;


/**
 * Classe principal do programa
 * 
 * @author Marcelo Suzuki
 *
 */
public class SmisApplication extends Application {
	
	/**
	 * Método que carrega a janela principal, chamado pelo JavaFX
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) {

		//Tamanho da janela maximixada
//		double width = Screen.getPrimary().getVisualBounds().getWidth(); 
//		double height = Screen.getPrimary().getVisualBounds().getHeight(); 
		double width = 700;
		double height = 300;
		
		try {
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("view/main.fxml"));
			Scene scene = new Scene(root, width, height);
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("Info_i_blue.png")));
			primaryStage.setTitle(" SMIS 1.0");
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
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

		
		launch(args);
	}
}

