package edu.uci.ics.sourcerer.services.slicer.internal;

public class SlicerDebug {

	private static boolean print = false;
	
	public static void debug(String string) {
		if(print)
			System.out.println(string);
	}
	
	public static void print(){print=true;}
	public static void dontprint(){print=false;}

}
