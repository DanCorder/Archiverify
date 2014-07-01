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

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

public class FileCopyActionTest extends spock.lang.Specification {
	private static final tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private static final relativePath = Paths.get("testFile")
	private static final generatedHash = "generatedHash"
	private static FileHashGenerator hashGenerator;
	private static Path tempFile

	def setup() {
		tempFile = Files.createTempFile(null, null);
		hashGenerator = Mock(FileHashGenerator)
		hashGenerator.calculateMd5(_) >> new String(generatedHash)
	}

	def cleanup() {
		Files.delete(tempFile)
		tempFile = null
	}

	def "Null parameters cause exception"() {
		when: "A null parameter is passed"
		new FileCopyAction(fromPath, toPath, hash, generator)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)

		where:
		fromPath | toPath   | hash                      | generator
		null     | tempFile | new String(generatedHash) | hashGenerator
		tempFile | null     | new String(generatedHash) | hashGenerator
		tempFile | tempFile | null                      | hashGenerator
		tempFile | tempFile | new String(generatedHash) | null
	}

	def "Relative paths cause exception"() {
		when: "A relative path is passed"
		new FileCopyAction(fromPath, toPath, new String(generatedHash), hashGenerator)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)

		where:
		fromPath     | toPath
		relativePath | tempFile
		tempFile     | relativePath
		relativePath | relativePath
	}

	def "Copy a file that doesn't exist"() {
		setup:
		def to = tempDir.resolve("toFile")
		def fca = new FileCopyAction(tempFile, to, new String(generatedHash), hashGenerator)

		when: "doACtion is called"
		fca.doAction();

		then: "A new file is created"
		Files.exists(to)

		cleanup:
		if (Files.exists(to)) {
			Files.delete(to)
		}
	}
	
	def "Copy a file that does exist"() {
		setup:
		def tempFile2 = Files.createTempFile(null, null);
		def fca = new FileCopyAction(tempFile, tempFile2, new String(generatedHash), hashGenerator)

		when: "doACtion is called"
		fca.doAction();

		then: "A new file is created"
		Files.exists(tempFile2)

		cleanup:
		if (Files.exists(tempFile2)) {
			Files.delete(tempFile2)
		}
	}
	
	def "Copied file doesn't match throws exception"() {
		setup:
		def to = tempDir.resolve("toFile")
		def fca = new FileCopyAction(tempFile, to, new String(generatedHash), hashGenerator)
		
		when:
		fca.doAction()
		
		then: "expect an exception"
		hashGenerator.calculateMd5(to) >> "badHash"
		thrown(Exception)
		
		cleanup:
		if (Files.exists(to)) {
			Files.delete(to)
		}
	}

	def "String value"() {
		setup:
		def to = tempDir.resolve("toFile")
		def fca = new FileCopyAction(tempFile, to, new String(generatedHash), hashGenerator)

		expect:
		fca.toString() == String.format("Copy %s to %s with hash %s", tempFile, to, generatedHash)
	}
}
