package br.unifesp.ict.seg.smis.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * 
 * Esta é uma classe que deriva de Stage, utilizada para ser um Stage genérico, 
 * contendo um AnchorPane.
 * 
 * @author Marcelo Suzuki
 *
 */
public class StageGeneric extends Stage {
	
	private String filename;
	private double width;
	private double height;
	private String title;
	
	/**
	 * Construtor
	 * 
	 * @param filename Nome do arquivo FXML
	 */
	public StageGeneric(String filename, double width, double height, String title) {
		this.filename = filename;
		this.width = width;
		this.height = height;
		this.title = title;
	}
	
	
	/**
	 * Este método configura o AnchorPane, recebido do arquivo fxml, 
	 * configura a Scene, com a largura e altura passada, e mostra a janela.
	 * 
	 */
	public void init(Window owner) {
		try {
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource(this.filename));
			Scene scene = new Scene(root, this.width, this.height);
			
			this.centerOnScreen();
			this.setScene(scene);
			this.setTitle(this.title);
			this.initOwner(owner);
			this.showAndWait();
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	//Getters and Setters
	
	public String getFilename() {
		return filename;
	}

	
}