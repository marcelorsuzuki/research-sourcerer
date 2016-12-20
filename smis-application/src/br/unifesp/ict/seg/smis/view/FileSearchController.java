package br.unifesp.ict.seg.smis.view;

import br.unifesp.ict.seg.smis.application.FileAdpterService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


/**
 * Controler para fazer a busca de um determinado arquivo no repositório
 * 
 * @author Marcelo Suzuki
 *
 */
public class FileSearchController {
	
	
	@FXML private TextField txtMethodId;
	@FXML private TextField txtFileId;
	@FXML private TextField txtMethodIdSlice;
	
	
	/**
	 * Busca um arquivo pelo Id do método no repositório
	 * 
	 * @param event
	 */
	@FXML public void bttActionFindMethodId(ActionEvent event) {
		int id = Integer.parseInt(txtMethodId.getText());
		FileAdpterService service = new FileAdpterService();
		service.findMethod(id);
	}

	
	/**
	 * Busca um arquivo pelo Id do arquivo no repositório
	 * @param event
	 */
	@FXML public void bttActionFindFileId(ActionEvent event) {
		int id = Integer.parseInt(txtFileId.getText());
		FileAdpterService service = new FileAdpterService();
		service.findFile(id);
	}


	/**
	 * Botão para fechar a janela
	 * @param event
	 */
	@FXML public void bttActionClose(ActionEvent event) { 
		((Button) event.getSource()).getScene().getWindow().hide();
		Platform.exit();
	}

}
