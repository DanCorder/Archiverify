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
