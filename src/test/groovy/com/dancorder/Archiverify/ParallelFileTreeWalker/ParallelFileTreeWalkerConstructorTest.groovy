package com.dancorder.Archiverify.ParallelFileTreeWalker;

import java.nio.file.Files
import java.nio.file.Path

public class ParallelFileTreeWalkerConstructorTest extends spock.lang.Specification {

	private static Path validTempRootPath1
	private static Path validTempRootPath2
	private static Path invalidTempRootPath1
	private static Path invalidTempRootPath2
	private static visitor

	def setupSpec() {
		validTempRootPath1 = createRootDirectory()
		validTempRootPath2 = createRootDirectory()
		invalidTempRootPath1 = createRootDirectory()
		invalidTempRootPath2 = createRootDirectory()
		cleanUpDirectory(invalidTempRootPath1)
		cleanUpDirectory(invalidTempRootPath2)
		visitor = Mock(ParallelFileTreeVisitor)
	}

	def cleanupSpec() {
		cleanUpDirectory(validTempRootPath1)
		cleanUpDirectory(validTempRootPath2)
		cleanUpDirectory(invalidTempRootPath1)
		cleanUpDirectory(invalidTempRootPath2)
	}

	private Path createRootDirectory() throws IOException {
		return Files.createTempDirectory(null);
	}

	private void cleanUpDirectory(Path directory) throws IOException {
		Files.deleteIfExists(directory)
	}

	def "Invalid constructor parameters"() {
		when:
		new ParallelFileTreeWalker(path1, path2, fileTreeVisitor)

		then: "expect IllegalArgumentException"
		thrown(IllegalArgumentException)

		where:
		path1                | path2                | fileTreeVisitor
		null                 | validTempRootPath2   | visitor
		invalidTempRootPath1 | validTempRootPath2   | visitor
		validTempRootPath1   | null                 | visitor
		validTempRootPath1   | invalidTempRootPath2 | visitor
		invalidTempRootPath1 | invalidTempRootPath2 | visitor
		validTempRootPath1   | validTempRootPath2   | null
	}
}