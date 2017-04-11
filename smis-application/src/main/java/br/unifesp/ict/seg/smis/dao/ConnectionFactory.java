package br.unifesp.ict.seg.smis.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


/**
 * 
 * Classe que permite abrir e obter uma conexão com o banco de dados.
 * 
 * @author Marcelo Suzuki
 *
 */
public class ConnectionFactory {
	
	// Parâmetros de conexão
	private static String url = null;
	private static String user = null;
	private static String password = null;
	
	
	/**
	 * Abre uma conexão, utilizando os parâmetros passados no arquivo "smis.properties".
	 * 
	 * @return Uma conexão com o banco de dados
	 */
	public static Connection openConnection() {
		
		try {
			
			// Se caso ainda não utilizou nenhuma string de conexão
			if (url == null) {
				
				Properties prop = new Properties();
	    		InputStream input = new FileInputStream("smis.properties");
	    		
	    		prop.load(input);
	    		url = prop.getProperty("database-url");
	    		user = prop.getProperty("database-user");
	    		password = prop.getProperty("database-password");

	    		input.close();
			}

			
			return DriverManager.getConnection(url, user, password);
			
		} catch (SQLException | IOException e) {
			e.printStackTrace();	
			throw new RuntimeException(e);
		}
	}
	
	
	
}

