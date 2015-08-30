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

import java.io.IOException;
import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.*
import java.util.List;


class FileSystem {
	public static boolean fileExists(Path filePath) {
		def file = new File(filePath.normalize().toString())
		return file.exists() && !file.isDirectory()
	}
	
	public static Path createTempFile() {
		return Files.createTempFile(null, null)
	}

	public static Path createTempFile(String data) {
		createDataFile(createTempFile(), data)
	}
	
	public static Path createDataFile(Path file, String data) {
		def writer = Files.newBufferedWriter(file, Charset.defaultCharset())
		writer.write(data)
		writer.close()
		return file
	}
	
	public static List<String> readFile(Path file) {
		return Files.readAllLines(file, Charset.defaultCharset)
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
	
	public static Path createFileIn(Path directory, String fileName) {
		return Files.createFile(directory.resolve(fileName))
	}
	
	public static Path createFileIn(Path directory, Path fileName) {
		return Files.createFile(directory.resolve(fileName))
	}
	
	public static Path createDirectoryIn(Path directory) {
		return Files.createTempDirectory(directory, null)
	}

	public static Path createDirectoryIn(Path directory, String subDirectoryName) {
		return Files.createDirectory(directory.resolve(subDirectoryName))
	}

	public static Path createDirectoryIn(Path directory, Path subDirectoryPath) {
		return Files.createDirectory(directory.resolve(subDirectoryPath))
	}

	public static void cleanUpDirectory(Path directory) {
		if (!deleteRecursive(directory.toFile())) {
			throw new IOException("Failed to clean up directory " + directory.toString())
		}
	}
	
	private static boolean deleteRecursive(File path) {
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
	private static Path getBuildOutputDirectory() {
		getArchiverifyDirectory().resolve("build").resolve("libs")
	}
		
	// Return something like /Users/dan/Development/Archiverify/
	public static Path getArchiverifyDirectory() {
		def fiveParentsUp = getClassLocation().getParent().getParent().getParent().getParent().getParent().getParent()
		
		if (Files.exists(fiveParentsUp.resolve("build"))) {
			return fiveParentsUp
		}
		
		return fiveParentsUp.getParent().getParent()
	}
	
	// Returns something like this running under Eclipse /Users/dan/Development/Archiverify/bin/com/dancorder/Archiverify/testHelpers/FileSystem.class
	// Returns something like this running under Gradle  /Users/dan/Development/Archiverify/build/classes/integrationTest/com/dancorder/Archiverify/testHelpers/FileSystem.class
	public static Path getClassLocation() {
		def classpath = FileSystem.class.getResource("FileSystem.class").toURI()
		Paths.get(classpath).toAbsolutePath()
	}
}
