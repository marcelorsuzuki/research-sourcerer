package br.unifesp.ict.seg.smis.view;

import java.sql.Connection;

import br.unifesp.ict.seg.smis.dao.ConnectionFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class TableMethodsController {
	
	@FXML private Label labelNumberMethods;
	@FXML private Label stringMethods;
	@FXML private Label floatMethods;
	@FXML private Label doubleMethods;
	@FXML private Label integerMethods;
	@FXML private Label longMethods;
	@FXML private Label booleanMethods;
	

	
	@FXML public void bttActionRecreateTable(ActionEvent event) { 
		Connection conn = ConnectionFactory.openConnection();
		System.out.println(conn);
	}

	@FXML public void bttActionClose(ActionEvent event) { 
		((Button) event.getSource()).getScene().getWindow().hide();
	}


}
