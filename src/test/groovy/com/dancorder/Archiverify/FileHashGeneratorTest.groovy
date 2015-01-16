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
import java.nio.file.Paths
import com.dancorder.Archiverify.testHelpers.*

class FileHashGeneratorTest extends spock.lang.Specification {

	def "hash generation"() {
		setup: "correct hash value calculated by http://onlinemd5.com/"
		def file = FileSystem.createTempFile("testData")
		def generator = new FileHashGenerator()

		expect: "check the generator creates the correct hash"
		generator.calculateMd5(file) == "3a760fae784d30a1b50e304e97a17355"
		
		cleanup:
		FileSystem.cleanUpFile(file)
	}
	
	def "hash generation for non-existant file"() {
		setup:
		def nonExistantFile = FileSystem.getTempDirectory().resolve("hashInput.txt")
		def generator = new FileHashGenerator()

		expect: "check the generator returns null"
		generator.calculateMd5(nonExistantFile) == null
	}
}
