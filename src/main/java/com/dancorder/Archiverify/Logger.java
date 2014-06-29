package com.dancorder.Archiverify;

import java.text.SimpleDateFormat;
import java.util.Date;

class Logger {
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
	
	static void log(String message) {
		logLine(message);
	}
	
	static void logWarning(String message) {
		logLine("========================================");
		logLine(String.format("WARNING: %s", message));
		logLine("========================================");
	}
	
	static void logError(String message) {
		logLine("*****************************************");
		logLine(String.format("ERROR: %s", message));
		logLine("*****************************************");
	}
	
	private static void logLine(String message) {
		System.out.println(String.format("%s %s", dateFormatter.format(new Date()), message));
	}
}
