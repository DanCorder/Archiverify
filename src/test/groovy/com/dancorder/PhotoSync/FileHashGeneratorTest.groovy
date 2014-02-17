package com.dancorder.PhotoSync;

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
		def expected = "3a760fae784d30a1b50e304e97a17355"
		def generator = new FileHashGenerator()

		expect: "check the generator creates the correct hash"
		generator.calculateMd5(tempInputFile) == expected
	}

	private void writeTempFile(String data) throws IOException {
		def writer = Files.newBufferedWriter(tempInputFile, Charset.defaultCharset())
		writer.write(data)
		writer.close()
	}
}
