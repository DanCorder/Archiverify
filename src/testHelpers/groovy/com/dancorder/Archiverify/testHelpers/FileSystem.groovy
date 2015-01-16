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

package com.dancorder.Archiverify.testHelpers

import java.nio.file.*
import java.nio.file.attribute.*


class FileSystem {
	public static Path createTempFile() {
		return Files.createTempFile(null, null)
	}

	public static Path createTempFile(String data) {
		createDataFile(createTempFile(), data)
	}
	
	public static void cleanUpFile(Path file) {
		Files.deleteIfExists(file)
	}
	
	public static Path getTempDirectory() {
		return Paths.get(System.getProperty("java.io.tmpdir"))
	}
	
	public static Path createRootDirectory() {
		return Files.createTempDirectory(null)
	}
	
	public static Path createRelativeRootDirectory(Path root) {
		return Files.createTempDirectory(root, null)
	}
	
	public static Path createFile(Path directory, String fileName) throws IOException {
		return Files.createFile(directory.resolve(fileName))
	}
	
	public static Path createSubDirectory(Path directory, String subDirectoryName) throws IOException {
		return Files.createDirectory(directory.resolve(subDirectoryName))
	}
	
	public static void cleanUpDirectory(Path directory) throws IOException {
		if (!deleteRecursive(directory.toFile())) {
			throw new IOException("Failed to clean up directory " + directory.toString())
		}
	}
	
	private static boolean deleteRecursive(File path) throws FileNotFoundException{
		if (!path.exists()) return true
		
		boolean ret = true
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && deleteRecursive(f)
			}
		}
		return ret && path.delete()
	}
	
	static Path getJarFile() {
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
	
	// Return something like /Users/dan/Development/Archiverify/build/libs/
	static Path getBuildOutputDirectory() {
		getArchiverifyDirectory().resolve("build").resolve("libs")
	}
		
	// Return something like /Users/dan/Development/Archiverify/
	private static Path getArchiverifyDirectory() {
		def fiveParentsUp = getClassLocation().getParent().getParent().getParent().getParent().getParent().getParent()
		
		if (Files.exists(fiveParentsUp.resolve("build"))) {
			return fiveParentsUp
		}
		
		return fiveParentsUp.getParent().getParent()
	}
	
	// Returns something like this running under Eclipse /Users/dan/Development/Archiverify/bin/com/dancorder/Archiverify/testHelpers/Run.class
	// Returns something like this running under Gradle  /Users/dan/Development/Archiverify/build/classes/integrationTest/com/dancorder/Archiverify/testHelpers/Run.class
	private static Path getClassLocation() {
		def classpath = FileSystem.class.getResource("FileSystem.class").getPath()
		Paths.get(classpath).toAbsolutePath()
	}
}
