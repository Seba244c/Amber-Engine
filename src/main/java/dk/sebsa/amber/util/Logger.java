package dk.sebsa.amber.util;

import dk.sebsa.amber.io.DevWindow;

public class Logger {
	private static boolean disableLogging = false;
	
	public static void debugLog(String file, String function, String message) {
		if(!disableLogging) {
			if(DevWindow.useDevWindow) {
				System.out.println("^ ["+file+".java/"+function+"] "+message);
			} else {
				System.out.println(" ["+file+".java/"+function+"] "+message);
			}
		}
	}
	public static void debugLog(String file, String message) {
		if(!disableLogging) {
			if(DevWindow.useDevWindow) {
				System.out.println("^ ["+file+".java] "+message);
			} else {
				System.out.println(" ["+file+".java] "+message);
			}
		}
	}
	public static void errorLog(String file, String function, String message) {
		if(!disableLogging) {
			if(DevWindow.useDevWindow) {
				System.err.println("~ ["+file+".java/"+function+"] "+message);
			} else {
				System.err.println(" ["+file+".java/"+function+"] "+message);
			}
		}
	}
	public static void errorLog(String file, String message) {
		if(!disableLogging) {
			if(DevWindow.useDevWindow) {
				System.err.println("~ ["+file+".java] "+message);
			} else {
				System.err.println(" ["+file+".java] "+message);
			}
		}
	}
	
	public static void infoLog(String file, String function, String message) {
		if(!disableLogging) {
			if(DevWindow.useDevWindow) {
				System.out.println("* ["+file+".java/"+function+"] "+message);
			} else {
				System.out.println(" ["+file+".java/"+function+"] "+message);
			}
		}
	}
	
	public static void infoLog(String file, String message) {
		if(!disableLogging) {
			if(DevWindow.useDevWindow) {
				System.out.println("* ["+file+".java] "+message);
			} else {
				System.out.println(" ["+file+".java] "+message);
			}
		}
	}
	
	public static void disableLogging() {
		disableLogging = true;
	}
}
