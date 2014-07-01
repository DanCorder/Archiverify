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

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

class FileHashGeneratorTest extends spock.lang.Specification {

	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final static tempInputFile = tempDir.resolve("hashInput.txt")

	def cleanup() {
		Files.deleteIfExists(tempInputFile)
	}

	def "hash generation"() {
		setup: "correct hash value calculated by http://onlinemd5.com/"
		writeTempFile("testData")
		def generator = new FileHashGenerator()

		expect: "check the generator creates the correct hash"
		generator.calculateMd5(tempInputFile) == "3a760fae784d30a1b50e304e97a17355"
	}
	
	def "hash generation for non-existant file"() {
		setup:
		def generator = new FileHashGenerator()

		expect: "check the generator creates the correct hash"
		generator.calculateMd5(tempInputFile) == null
	}

	private void writeTempFile(String data) throws IOException {
		def writer = Files.newBufferedWriter(tempInputFile, Charset.defaultCharset())
		writer.write(data)
		writer.close()
	}
}
