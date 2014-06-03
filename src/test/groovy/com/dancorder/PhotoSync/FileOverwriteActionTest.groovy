package com.dancorder.PhotoSync;

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
