package br.unifesp.ict.seg.smis.view;

import java.io.FileNotFoundException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import br.unifesp.ict.seg.smis.FileAdpterService;


public class SearchIdController {
	
	@FXML private TextField txtMethodId;
	@FXML private TextField txtFileId;
	
	@FXML public void bttActionFindMethod(ActionEvent event) {
		int id = Integer.parseInt(txtMethodId.getText());
		try {
			FileAdpterService service = new FileAdpterService();
			service.findMethod(id);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}

	@FXML public void bttActionFindFile(ActionEvent event) { 
		int id = Integer.parseInt(txtFileId.getText());
		FileAdpterService service;
		try {
			service = new FileAdpterService();
			service.findFile(id);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	@FXML public void bttActionClose(ActionEvent event) { 
		((Button) event.getSource()).getScene().getWindow().hide();
		Platform.exit();
	}
	
}
