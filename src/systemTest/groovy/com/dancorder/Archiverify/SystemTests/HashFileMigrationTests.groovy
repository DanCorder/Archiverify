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

package com.dancorder.Archiverify.SystemTests

import com.dancorder.Archiverify.testHelpers.*
import java.nio.file.*

public class HashFileMigrationTests extends spock.lang.Specification {
	private static Path path1Root
	private static Path path2Root
	private final oldHashFileName = "hashes.txt"
	private final newHashFileName = ".hashes"
	private final fileName = "test1"
	private final fileContents = "testData"
	private final fileHash = "3a760fae784d30a1b50e304e97a17355"
	
	def setup () {
		path1Root = FileSystem.createRootDirectory()
		path2Root = FileSystem.createRootDirectory()
	}
	
	def cleanup() {
		FileSystem.cleanUpDirectory(path1Root)
		FileSystem.cleanUpDirectory(path2Root)
		path1Root = null
		path2Root = null
	}
	
	def "Migrate hash files from v1 to v2"() {
		given: "An archive with hashes in hashes.txt"
		def hashFileContents = fileHash + "\t" + fileName
		FileSystem.createDataFile(path1Root.resolve(fileName), fileContents)
		FileSystem.createDataFile(path2Root.resolve(fileName), fileContents)
		FileSystem.createDataFile(path1Root.resolve(oldHashFileName), hashFileContents)
		FileSystem.createDataFile(path2Root.resolve(oldHashFileName), hashFileContents)
		
		when: "Archiverify is told to read from hashes.txt"
		def result = Run.archiverify(path1Root.toString(), path2Root.toString(), "-fr", "hashes.txt", "-y")
		
		then: "Hashes are moved to .hashes"
		!result.stdout.toLowerCase().contains("error")
		!FileSystem.fileExists(path1Root.resolve(oldHashFileName))
		!FileSystem.fileExists(path2Root.resolve(oldHashFileName))
		FileSystem.fileExists(path1Root.resolve(newHashFileName))
		FileSystem.fileExists(path2Root.resolve(newHashFileName))
		def fileContents = FileSystem.readFile(path1Root.resolve(newHashFileName))
		fileContents.size() == 1
		fileContents[0] == hashFileContents
		def fileContents2 = FileSystem.readFile(path2Root.resolve(newHashFileName))
		fileContents2.size() == 1
		fileContents2[0] == hashFileContents
	}
}
