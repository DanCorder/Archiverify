package com.dancorder.PhotoSync;

import static org.junit.Assert.*

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.dancorder.PhotoSync.Action.FileCopyAction

public class FileCopyActionTest extends spock.lang.Specification {
	private static final tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private static final relativePath = Paths.get("testFile")
	private static Path tempFile

	def setup() {
		tempFile = Files.createTempFile(null, null);
	}

	def cleanup() {
		Files.delete(tempFile)
		tempFile = null
	}


	def "Null paths"() {
		when: "A null parameter is passed"
		new FileCopyAction(fromPath, toPath)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)

		where:
		fromPath | toPath
		null     | tempFile
		tempFile | null
		null     | null
	}

	def "Relative paths"() {
		when: "A relative path is passed"
		new FileCopyAction(fromPath, toPath)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)

		where:
		fromPath     | toPath
		relativePath | tempFile
		tempFile     | relativePath
		relativePath | relativePath
	}

	def "Copy a file"() {
		setup:
		def to = tempDir.resolve("toFile")
		def fca = new FileCopyAction(tempFile, to)

		when: "doACtion is called"
		fca.doAction();

		then: "A new file is created"
		Files.exists(to)

		cleanup:
		if (Files.exists(to)) {
			Files.delete(to)
		}
	}

	def "String value"() {
		setup:
		def to = tempDir.resolve("toFile")
		def fca = new FileCopyAction(tempFile, to)

		expect:
		fca.toString() == "Copy " + tempFile.toString() + " to " + to.toString()
	}
}
