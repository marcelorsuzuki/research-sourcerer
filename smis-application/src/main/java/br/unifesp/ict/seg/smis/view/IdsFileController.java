package br.unifesp.ict.seg.smis.view;

import java.io.File;
import java.io.IOException;

import br.unifesp.ict.seg.smis.process.MethodsProcess;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class IdsFileController {

	@FXML private TextField txtFileIds;
	@FXML private Label lblNumberFiles;

	@FXML public void bttCompile(ActionEvent event) {
		MethodsProcess mp = new MethodsProcess(txtFileIds.getText());
		try {
			mp.compileAll();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML public void bttActionClose(ActionEvent event) {
		((Button) event.getSource()).getScene().getWindow().hide();
	}

	@FXML public void bttActionSelect(ActionEvent event) {
		
		Window window = ((Button) event.getSource()).getScene().getWindow();
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Select one file");
		fc.getExtensionFilters().addAll(
				new ExtensionFilter("Text file", "*.txt"),
				new ExtensionFilter("All files", "*.*"));
		File file = fc.showOpenDialog(window);
		txtFileIds.setText(file.getAbsolutePath());
	}
	

}
