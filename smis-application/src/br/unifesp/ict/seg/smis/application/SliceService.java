package br.unifesp.ict.seg.smis.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

import javax.swing.JOptionPane;

import edu.uci.ics.sourcerer.services.slicer.SlicerFactory;
import edu.uci.ics.sourcerer.services.slicer.internal.SliceImpl;
import edu.uci.ics.sourcerer.services.slicer.internal.SlicerDebug;
import edu.uci.ics.sourcerer.services.slicer.model.Slice;
import edu.uci.ics.sourcerer.services.slicer.model.Slicer;

public class SliceService {
	
	private String zipFile = null;
	
	public SliceService() {}
	
	public SliceService(String zipFile) {
		this.zipFile = zipFile;
	}
	
	
	public boolean findMethod(int entityID) {
		long start = System.currentTimeMillis();
		
		Slicer slicer = SlicerFactory.createSlicer();
		if (slicer == null) {
			return false;
		}
		
		Slice result = slicer.slice(Collections.singleton(entityID));
		if (result == null) {
			return false;
		}

		
		SliceImpl si = (SliceImpl)result;
		SlicerDebug.debug("[SlicerFactory]slice:\n" + si.getInternalEntities().toString().replace(",", "\n").replace("[", "").replace("]", ""));
		byte[] input = result.toZipFile();
		
		long end = System.currentTimeMillis();
		
		System.out.println("total of " + (end - start) + " ms");
		
		if (zipFile == null) {
			zipFile = JOptionPane.showInputDialog("Type zip's fqn", System.getProperty("user.home") + File.separator + "slicertest.zip");
			JOptionPane.showMessageDialog(null, result.getInternalEntities().toString().replace(",", "\n").trim());
		}
		FileOutputStream fos;
		
		try {
			fos = new FileOutputStream(new File(zipFile));
			fos.write(input);
			fos.close();
			return true;
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}	
		
		return false;
	}


}
