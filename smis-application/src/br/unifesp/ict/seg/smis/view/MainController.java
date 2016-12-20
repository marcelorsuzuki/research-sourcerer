package br.unifesp.ict.seg.smis.view;

import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

/**
 * 
 * Controler da janela principal
 * @author marcelo
 *
 */
public class MainController {
	
	@FXML private MethodSliceController searchId;
	
	
	/**
	 * Chama a janela de slice
	 * 
	 * @param event
	 */
	@FXML public void bttActionSliceMethod(ActionEvent event) {
		StageGeneric stage = new StageGeneric("method-slice.fxml", 600, 200, "Slice Method");
		Button button = (Button) event.getTarget();
    	stage.init(button.getScene().getWindow());
	}
	
	@FXML public void bttActionCreateJars(ActionEvent event) {
		StageGeneric stage = new StageGeneric("ids-file.fxml", 600, 150, "Select file with id's...");
		Button button = (Button) event.getTarget();
    	stage.init(button.getScene().getWindow());

	}
	
	
	/**
	 * Chama a janela de procura de arquivo
	 * 
	 * @param event
	 */
	@FXML public void bttActionSearchFile(ActionEvent event) {
		StageGeneric stage = new StageGeneric("file-sarch.fxml", 600, 150, "File Search");
		Button button = (Button) event.getTarget();
    	stage.init(button.getScene().getWindow());
	}
	
	@FXML public void bttActionTableMethods(ActionEvent event) {
		StageGeneric stage = new StageGeneric("table-methods.fxml", 600, 350, "Table Methods");
		Button button = (Button) event.getTarget();
    	stage.init(button.getScene().getWindow());
	}
	
	
	/**
	 * Fecha o progrma
	 * 
	 * @param event
	 */
	@FXML public void bttActionQuit(ActionEvent event) {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm");
		alert.setHeaderText("Do you want to finish this program?");
		
		
		Optional<ButtonType> res = alert.showAndWait();
		
		if (res.get() == ButtonType.OK) {
			Platform.exit();
		}
	}
	

}
