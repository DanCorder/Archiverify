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

public class FileOverwriteActionTest extends spock.lang.Specification {
	private static final tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
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

	def "String value"() {
		setup:
		def to = tempDir.resolve("toFile")
		def foa = new FileOverwriteAction(tempFile, to, new String(generatedHash), hashGenerator)

		expect:
		foa.toString() == String.format("Overwrite %s with %s with hash %s", to, tempFile, generatedHash)
	}
}
