//    Archiverify is an archive synching and verification tool
//    Copyright (C) 2014  Daniel Corder (contact: archiverify@dancorder.com)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
