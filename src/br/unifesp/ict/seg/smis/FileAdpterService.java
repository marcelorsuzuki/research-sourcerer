package br.unifesp.ict.seg.smis;

import edu.uci.ics.sourcerer.services.file.adapter.FileAdapter;
import edu.uci.ics.sourcerer.services.file.adapter.FileAdapter.Result;

public class FileAdpterService {

	
	public void findMethod(int id) {
		Result result = FileAdapter.lookupResultByEntityID(id);
		System.out.println(result.getName());
	}

	public void findFile(int id) { 
		Result result = FileAdapter.lookupResultByFileID(id);
		System.out.println(result);
	}
	
	
}
