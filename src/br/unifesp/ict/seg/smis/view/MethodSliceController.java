package br.unifesp.ict.seg.smis.view;

import br.unifesp.ict.seg.smis.SliceService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


/**
 * Controler para a janela que faz um Slice de um determinado método.
 * 
 * @author Marcelo Suzuki
 *
 */
public class MethodSliceController {
	
	
	@FXML private TextField txtMethodId;
	@FXML private TextField txtMethodName;

	
	/**
	 * Faz o slice de um método, procurando-o pelo seu Id.
	 * 
	 * @param event
	 */
	@FXML public void bttActionFindMethodId(ActionEvent event) {
		
		int entityID = Integer.parseInt(txtMethodId.getText());
		
		SliceService service = new SliceService();
		service.findMethod(entityID);
	}
	

	/**
	 * Faz o slice de um método, procurando-o pelo seu nome.
	 * 
	 * @param event
	 */
	@FXML public void bttActionFindMethodName(ActionEvent event) {
		
		String entityName = txtMethodName.getText();
		
		//TODO Buscar pelo id do método no banco de dados através de seu nome, e depois fazer o Slice
		System.out.println(entityName);
//		SliceService service = new SliceService();
//		service.findMethod(entityID);
	}
	
	
	/**
	 * Fecha esta janela.
	 * 
	 * @param event
	 */
	@FXML public void bttActionClose(ActionEvent event) { 
		((Button) event.getSource()).getScene().getWindow().hide();
	}


}
