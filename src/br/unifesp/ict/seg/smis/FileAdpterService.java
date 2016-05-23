package br.unifesp.ict.seg.smis;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import edu.uci.ics.sourcerer.services.file.adapter.FileAdapter;
import edu.uci.ics.sourcerer.services.file.adapter.FileAdapter.Result;
import edu.uci.ics.sourcerer.tools.java.repo.model.JavaRepositoryFactory;
import edu.uci.ics.sourcerer.util.io.arguments.ArgumentManager;
import edu.uci.ics.sourcerer.utils.db.DatabaseConnectionFactory;

public class FileAdpterService {

	public FileAdpterService() throws FileNotFoundException {
		InputStream is = new FileInputStream("smis.properties");
		ArgumentManager.PROPERTIES_STREAM.setValue(is);
		JavaRepositoryFactory.INPUT_REPO.permit();
		DatabaseConnectionFactory.DATABASE_URL.permit();
		DatabaseConnectionFactory.DATABASE_USER.permit();
		DatabaseConnectionFactory.DATABASE_PASSWORD.permit();
		ArgumentManager.initializeProperties();
	}
	
	
	public void findMethod(int id) {
		Result result = FileAdapter.lookupResultByEntityID(id);
		System.out.println(result.getName());
	}

	public void findFile(int id) { 
		Result result = FileAdapter.lookupResultByFileID(id);
		System.out.println(result);
	}
	
	
}
