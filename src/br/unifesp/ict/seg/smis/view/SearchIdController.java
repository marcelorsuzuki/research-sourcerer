package br.unifesp.ict.seg.smis.view;

import java.io.FileNotFoundException;

import br.unifesp.ict.seg.smis.FileAdpterService;
import br.unifesp.ict.seg.smis.SliceService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class SearchIdController {
	
	@FXML private TextField txtMethodIdFile;
	@FXML private TextField txtFileId;
	@FXML private TextField txtMethodIdSlice;
	
	@FXML public void bttActionFindMethodFile(ActionEvent event) {
		int id = Integer.parseInt(txtMethodIdFile.getText());
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

	@FXML public void bttActionFindMethodSlice(ActionEvent event) {
		
		int entityID = Integer.parseInt(txtMethodIdSlice.getText());
		
		SliceService service;
		try {
			service = new SliceService();
			service.findMethod(entityID);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	
	}

}
