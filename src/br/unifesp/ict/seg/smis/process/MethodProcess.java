package br.unifesp.ict.seg.smis.process;

public class MethodProcess {

	private String filename;

	public MethodProcess() {}
	
	public MethodProcess(String filename) {
		this.filename = filename;
	}

	
	public void compileAll() {
		//Obtém a pasta do arquivo
		int pos = filename.lastIndexOf("/");
		System.out.println(filename.substring(0, pos));
		
		//Faz a leitura do arquivo para obter os ids de "entity_metrics"
		
		//Loop para cada id de "entity_metrics"
		
			//Obtém o id de entity
		
			//Faz o slicer do entity
		
			//Extrai o zip obtido do slicer
		
			//Configura o "build.xml" do ant
		
			//Cria o jar do entity
		
	}
}
