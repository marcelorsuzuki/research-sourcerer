
package br.unifesp.ict.seg.smis;
	
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.unifesp.ict.seg.smis.view.StageGeneric;
import edu.uci.ics.sourcerer.tools.java.repo.model.JavaRepositoryFactory;
import edu.uci.ics.sourcerer.util.io.arguments.ArgumentManager;
import edu.uci.ics.sourcerer.utils.db.DatabaseConnectionFactory;
import javafx.application.Application;
import javafx.stage.Stage;


public class SmisApplication extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		StageGeneric stage = new StageGeneric("search-id.fxml", 480, 250, " SMIS 1.0");
		stage.init();
	}
	
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

