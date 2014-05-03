package com.dancorder.PhotoSync

import java.nio.file.Paths

import com.dancorder.PhotoSync.ParallelFileTreeWalker.FileExistence

class SyncLogicDirectoryTest extends spock.lang.Specification {
	
//	The rules for the directory synching logic are simple:
//    - If a directory exists in both roots do nothing
//    - If a directory exists in only one root then create it in the other

	private final static tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
	private final static absolutePath1 = tempDir.resolve("testRoot1").resolve("dir1")
	private final static absolutePath2 = tempDir.resolve("testRoot2").resolve("dir1")
	
	def "directory exists in both roots"() {
		setup:
		def logic = new SyncLogic(null)
		def expectedResult = new ArrayList<Action>()
		
		when:
		def result = logic.compareDirectories(absolutePath1, absolutePath2, FileExistence.BothPaths)

		then:
		expectedResult == result
	}
	
	def "directory exists in root 1"() {
		setup:
		def logic = new SyncLogic(null)
		def expectedResult = new ArrayList<Action>()
		expectedResult.add(new CreateDirectoryAction(absolutePath2))
		
		when:
		def result = logic.compareDirectories(absolutePath1, absolutePath2, FileExistence.Path1Only)

		then:
		expectedResult == result
	}
	
	def "directory exists in root 2"() {
		setup:
		def logic = new SyncLogic(null)
		def expectedResult = new ArrayList<Action>()
		expectedResult.add(new CreateDirectoryAction(absolutePath1))
		
		when:
		def result = logic.compareDirectories(absolutePath1, absolutePath2, FileExistence.Path2Only)

		then:
		expectedResult == result
	}
}