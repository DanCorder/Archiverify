//    Archiverify is an archive synching and verification tool
//    Copyright (C) 2015  Daniel Corder (contact: archiverify@dancorder.com)
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

package com.dancorder.Archiverify.IntegrationTests.Helpers

import java.nio.file.*
import java.nio.file.attribute.*

class Run {
	public static RunResult archiverify() {
		def command = "java -jar " + getJarFile().toString()
		
		def process = Runtime.getRuntime().exec(command)
		process.waitFor()
		
		return new RunResult(process)
	}
	
	private static Path getJarFile() {
		Path newestJarFile = null;
		FileTime newestLastModified = null;
		
		def directoryStream = Files.newDirectoryStream(getBuildOutputDirectory(), "Archiverify-v*.jar")
		
		try{
			for (Path file: directoryStream) {
				def lastModified = getLastModified(file)
				if (newestLastModified == null || lastModified > newestLastModified) {
					newestLastModified = lastModified
					newestJarFile = file;
				}
			}
		}
		finally {
			directoryStream.close();
		}

		return newestJarFile;
	}
	
	private static FileTime getLastModified(Path file) {
		def attributes = Files.readAttributes(file, BasicFileAttributes.class)
		return attributes.lastModifiedTime()
	}
	
	private static Path getBuildOutputDirectory() {
		// Something like /Users/dan/Development/Archiverify/build/libs/
		getArchiverifyDirectory().resolve("build").resolve("libs")
	}
		
	private static Path getArchiverifyDirectory() {
		// Something like /Users/dan/Development/Archiverify/
		getClassLocation().getParent().getParent().getParent().getParent().getParent().getParent().getParent();
	}
		
	private static Path getClassLocation() {
		// Something like /Users/dan/Development/Archiverify/bin/com/dancorder/Archiverify/IntegrationTests/Helpers/Run.class
		Paths.get(Run.class.getResource("Run.class").getPath()).toAbsolutePath()
	}
}
