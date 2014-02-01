package com.dancorder.PhotoSync;

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

import java.nio.file.Files
import java.nio.file.Paths

import com.dancorder.PhotoSync.Action.CreateDirectoryAction

public class CreateDirectoryActionTest extends spock.lang.Specification {
	private static final tempDir = Paths.get(System.getProperty("java.io.tmpdir"))

	def "Null path"() {
		when: "A null parameter is passed"
		new CreateDirectoryAction(null)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)
	}
	
	def "Relative path"() {
		setup:
		def directoryPath = Paths.get("testDirectory")

		when: "A relative path is passed"
		new CreateDirectoryAction(directoryPath)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)
	}
	
	def "Create a directory"() {
		setup:
		def directoryPath = tempDir.resolve("testDirectory")
		def cda = new CreateDirectoryAction(directoryPath)

		when: "doACtion is called"
		cda.doAction()

		then: "A new directory is created"
		Files.exists(directoryPath)

		cleanup:
		if (Files.exists(directoryPath)) {
			Files.delete(directoryPath)
		}
	}
	
	def "String value"() {
		setup:
		def directoryPath = tempDir.resolve("testDirectory")
		def cda = new CreateDirectoryAction(directoryPath)

		expect:
		cda.toString() == "Create directory: " + directoryPath.toString()
	}
}
