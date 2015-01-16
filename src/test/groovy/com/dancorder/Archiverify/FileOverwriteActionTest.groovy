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
import com.dancorder.Archiverify.testHelpers.*

public class FileOverwriteActionTest extends spock.lang.Specification {
	private static final generatedHash = "generatedHash"
	private static final tempDir = FileSystem.getTempDirectory()

	def "String value"() {
		setup:
		def toFile = tempDir.resolve("toFile")
		def fromFile = tempDir.resolve("fromFile")
		
		def hashGenerator = Mock(FileHashGenerator)
		hashGenerator.calculateMd5(_) >> new String(generatedHash)
		
		def foa = new FileOverwriteAction(fromFile, toFile, new String(generatedHash), hashGenerator)

		expect:
		foa.toString() == String.format("Overwrite %s with %s with hash %s", toFile, fromFile, generatedHash)
	}
}
